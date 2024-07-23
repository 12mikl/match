package com.back.partnerback.service.impl;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.back.partnerback.Exception.BusinessException;
import com.back.partnerback.common.ErrorCode;
import com.back.partnerback.constant.StatusConstant;
import com.back.partnerback.mapper.TeamMapper;
import com.back.partnerback.model.Dto.TeamAddDTO;
import com.back.partnerback.model.Dto.TeamQueryDTO;
import com.back.partnerback.model.Request.UpdateTeamRequest;
import com.back.partnerback.model.Request.UserJoinTeamRequest;
import com.back.partnerback.model.Team;
import com.back.partnerback.model.User;
import com.back.partnerback.model.UserTeam;
import com.back.partnerback.model.VO.TeamVO;
import com.back.partnerback.model.VO.UserTeamInfoVO;
import com.back.partnerback.service.TeamService;
import com.back.partnerback.service.UserService;
import com.back.partnerback.service.UserTeamService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author user
 * @description 针对表【team(队伍)】的数据库操作Service实现
 * @createDate 2024-07-17 11:29:37
 */
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
        implements TeamService {

    @Resource
    private UserTeamService userTeamService;

    @Resource
    private UserService userService;
    @Resource
    private RedissonClient redissonClient;

    @Override
    public List<Team> getTeamList(Team team) {
        LambdaQueryWrapper<Team> queryWrapper = new LambdaQueryWrapper<>();
        if (ObjectUtils.isNotEmpty(team.getTeamIds())) {
            queryWrapper.in(Team::getId, team.getTeamIds());
        }
        if (StringUtils.isNotBlank(team.getName())) {
            queryWrapper.like(Team::getName, team.getName());
        }
        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long addTeam(TeamAddDTO teamAddDTO, HttpServletRequest request) {
        User loginUserInfo = userService.getLoginUserInfo(request);

        final Long userId = loginUserInfo.getId();
        if (teamAddDTO == null) {
            throw new BusinessException(ErrorCode.NULL_PARAMS);
        }
        // 队伍名称，不超过20个
        String name = teamAddDTO.getName();
        if (StringUtils.isNotBlank(name) && name.length() > 20) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "队伍名称超过20个字");
        }
        // 队伍描述
        String description = teamAddDTO.getDescription();
        if (StringUtils.isNotBlank(description) && name.length() > 512) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "队伍描述超过512个字");
        }
        // 最大人数
        Integer maxNum = Optional.ofNullable(teamAddDTO.getMaxNum()).orElse(0);
        if (maxNum < 1 || maxNum > 20) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "最大人数超过限制");
        }
        // 超时时间
        Date expireTime = teamAddDTO.getExpireTime();
        if (new Date().after(expireTime)) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "超过时间不符合要求");
        }
        // 状态，默认公开
        Integer status = Optional.ofNullable(teamAddDTO.getStatus()).orElse(0);
        // 加密密码
        String password = teamAddDTO.getPassword();

        if (status == 2) {
            if (password.length() > 8) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "加密密码不符合要求");
            }
        }

        // 队伍数量
        LambdaQueryWrapper<Team> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Team::getUserId, userId);
        queryWrapper.eq(Team::getIsDelete, StatusConstant.USER_DELETE_NOT_DELETE);
        long count = this.count(queryWrapper);
        if (count >= 5) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "队伍数量不符合要求");
        }
        Team team = new Team();
        BeanUtils.copyProperties(teamAddDTO, team);
        team.setUserId(userId);
        boolean saveResult = this.save(team);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "新增队伍失败");
        }

        Long teamId = team.getId();
        UserTeam userTeam = new UserTeam();
        userTeam.setTeamId(teamId);
        userTeam.setUserId(userId);
        userTeam.setJoinTime(new Date());
        saveResult = userTeamService.save(userTeam);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "新增队伍失败");
        }
        return teamId;
    }

    @Override
    public List<TeamVO> myCreateTeam(String name, HttpServletRequest request) {
        // 获取当前登录用户
        User loginUserInfo = userService.getLoginUserInfo(request);
        LambdaQueryWrapper<Team> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(name)) {
            queryWrapper.like(Team::getName, name);
        }
        queryWrapper.eq(Team::getUserId, loginUserInfo.getId());
        List<Team> teamList = this.baseMapper.selectList(queryWrapper);
        if (CollectionUtil.isEmpty(teamList)) {
            return new ArrayList<>();
        }
        List<TeamVO> teamVOList = getTeamVOList(teamList, loginUserInfo);
        return teamVOList;
    }

    @Override
    public List<TeamVO> myJoinTeam(String name, HttpServletRequest request) {
        User loginUserInfo = userService.getLoginUserInfo(request);
        // 查询我加入的队伍id
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(loginUserInfo.getId());
        List<UserTeam> userTeamInfo = userTeamService.getUserTeamInfo(userTeam);
        if (CollectionUtil.isEmpty(userTeamInfo)) {
            return new ArrayList<>();
        }
        List<Long> teamIds = CollStreamUtil.toList(userTeamInfo, UserTeam::getTeamId);
        Team team = new Team();
        team.setTeamIds(teamIds);
        team.setName(name);
        List<Team> teamList = this.getTeamList(team);
        List<TeamVO> teamVOList = getTeamVOList(teamList, loginUserInfo);
        return teamVOList;
    }

    @Override
    public IPage<TeamVO> getPublicOrSecretTeams(TeamQueryDTO queryDTO, HttpServletRequest request) {
        User loginUserInfo = userService.getLoginUserInfo(request);
        LambdaQueryWrapper<Team> queryWrapper = new LambdaQueryWrapper<>();
        if (ObjectUtils.isEmpty(queryDTO.getStatus())) {
            throw new BusinessException(ErrorCode.NULL_PARAMS);
        }
        if (StringUtils.isNotBlank(queryDTO.getSearchText())) {
            queryWrapper.like(Team::getName, queryDTO.getSearchText());
        }
//        queryWrapper.eq(Team::getUserId,loginUserInfo.getId());
        queryWrapper.eq(Team::getStatus, queryDTO.getStatus());

        // 分页参数
        Page<Team> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        // 执行查询
        IPage<Team> teamPage = this.baseMapper.selectPage(page, queryWrapper);

        // 创建新的IPage<TeamVO>实例，并设置转换后的列表和其他分页信息
        IPage<TeamVO> teamVOPage = new Page<>(teamPage.getCurrent(), teamPage.getSize(), teamPage.getTotal());
        teamVOPage.setRecords(this.getTeamVOList(teamPage.getRecords(), loginUserInfo));
        return teamVOPage;
    }

    @Override
    public UserTeamInfoVO getUserTeamInfoById(Long id) {
        Team team = this.baseMapper.selectById(id);
        UserTeamInfoVO userTeamInfoVO = new UserTeamInfoVO();
        UserTeam userTeam = new UserTeam();
        userTeam.setTeamId(id);
        List<UserTeam> userTeamInfo = userTeamService.getUserTeamInfo(userTeam);
        if (CollectionUtil.isEmpty(userTeamInfo)) {
            return userTeamInfoVO;
        }
        Set<Long> userIdS = CollStreamUtil.toSet(userTeamInfo, UserTeam::getUserId);
        userTeamInfoVO.setName(team.getName());
        userTeamInfoVO.setMaxNum(team.getMaxNum());
        userTeamInfoVO.setAddTeamUserInfos(userService.getUserInfoByIds(userIdS));
        return userTeamInfoVO;
    }

    @Override
    public boolean updateTeam(UpdateTeamRequest updateTeamRequest, HttpServletRequest request) {
        if (updateTeamRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long teamId = updateTeamRequest.getId();
        Team team = getTeamById(teamId);
        User loginUserInfo = userService.getLoginUserInfo(request);
        if (!Objects.equals(team.getUserId(), loginUserInfo.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        String password = updateTeamRequest.getPassword();
        if (Objects.equals(updateTeamRequest.getStatus(), StatusConstant.TEAM_SECRET) && StringUtils.isBlank(password)) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "加密队伍密码不能为空");
        }
        Team updateTeam = new Team();
        BeanUtils.copyProperties(updateTeamRequest, updateTeam);
        boolean result = this.updateById(updateTeam);
        return result;
    }

    @Override
    public boolean joinUserTeam(UserJoinTeamRequest joinTeamRequest, HttpServletRequest request) {
        if (joinTeamRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long teamId = joinTeamRequest.getId();

        Team team = getTeamById(teamId);
        // 禁止加入私有队伍
        if (Objects.equals(team.getStatus(), StatusConstant.TEAM_PRIVATE)) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "禁止加入私有队伍");
        }
        //加入加密队伍，密码必须匹配
        String password = team.getPassword();
        if (Objects.equals(StatusConstant.TEAM_SECRET, team.getStatus()) && !Objects.equals(password, joinTeamRequest.getPassword())) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "密码不匹配");
        }
        // 未过期的队伍
        Date expireTime = team.getExpireTime();
        if (expireTime != null && expireTime.before(new Date())) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "队伍已过期");
        }
        User loginUserInfo = userService.getLoginUserInfo(request);
        Long userId = loginUserInfo.getId();

        // 只有一个线程获取锁
        RLock lock = redissonClient.getLock("match:join_team");
        try {
            while (true) {


                // 获取锁，设置时间
                if (lock.tryLock(10, 10, TimeUnit.MILLISECONDS)) {
                    // 最多能加入5个队伍
                    LambdaQueryWrapper<UserTeam> userTeamLambdaQueryWrapper = new LambdaQueryWrapper<>();
                    userTeamLambdaQueryWrapper.eq(UserTeam::getUserId, userId);
                    long hasJoinTeam = userTeamService.count(userTeamLambdaQueryWrapper);
                    if (hasJoinTeam >= 5) {
                        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "最多能加入5个队伍");
                    }
                    userTeamLambdaQueryWrapper.clear();
                    userTeamLambdaQueryWrapper.eq(UserTeam::getTeamId, teamId);
                    long teamNum = userTeamService.count(userTeamLambdaQueryWrapper);
                    // 未满的队伍
                    if (teamNum >= team.getMaxNum()) {
                        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "队伍人数已满");
                    }
                    // 不能重复加入队伍
                    userTeamLambdaQueryWrapper.clear();
                    userTeamLambdaQueryWrapper.eq(UserTeam::getTeamId, teamId);
                    userTeamLambdaQueryWrapper.eq(UserTeam::getUserId, userId);
                    long hasUserJoinTeam = userTeamService.count(userTeamLambdaQueryWrapper);
                    if (hasUserJoinTeam > 0) {
                        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "重复加入队伍");
                    }
                    UserTeam userTeam = new UserTeam();
                    userTeam.setJoinTime(new Date());
                    userTeam.setTeamId(teamId);
                    userTeam.setUserId(userId);
                    boolean result = userTeamService.save(userTeam);
                    if (!result) {
                        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "加入队伍失败");
                    }
                    return true;
                }
            }
        } catch (Exception e) {
            log.error("doJoinTeam error", e);
            return false;
        } finally {
            // 最后释放锁
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Override
    public boolean quitTeam(Long id, HttpServletRequest request) {
        // 校验参数和校验队伍
        Team team = getTeamById(id);
        User loginUserInfo = userService.getLoginUserInfo(request);
        Long userId = loginUserInfo.getId();
        LambdaQueryWrapper<UserTeam> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserTeam::getTeamId, id);
        queryWrapper.eq(UserTeam::getUserId, userId);
        long count = userTeamService.count(queryWrapper);
        if (count == 0) {
            throw new BusinessException(ErrorCode.NULL_PARAMS, "用户不在该队伍");
        }
        queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserTeam::getTeamId, id);
        long teamHasJoinNum = userTeamService.count(queryWrapper);
        //队伍只剩一人，解散
        if (teamHasJoinNum == 1) {
            this.removeById(id);
        } else {
            //如果是队长
            if (team.getUserId() == userId) {
                //把队伍转移给加入最早的用户
                QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
                userTeamQueryWrapper.eq("teamId", id);
                userTeamQueryWrapper.last("order by id asc limit 2");
                List<UserTeam> userTeamList = userTeamService.list(userTeamQueryWrapper);
                if (CollectionUtils.isEmpty(userTeamList) || userTeamList.size() <= 1) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR);
                }
                UserTeam nextUserTeam = userTeamList.get(1);
                Long nextTeamLeaderId = nextUserTeam.getUserId();
                //更新当前队伍的队长
                Team updateTeam = new Team();
                updateTeam.setId(id);
                updateTeam.setUserId(nextTeamLeaderId);
                boolean result = this.updateById(updateTeam);
                if (!result) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新队伍队长失败");
                }

            }
        }
        //移除关联
        return userTeamService.remove(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTeam(long id, HttpServletRequest request) {
        //校验队伍是否存在
        Team team = getTeamById(id);
        long teamId = team.getId();
        User loginUser = userService.getLoginUserInfo(request);
        if (!team.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH, "无访问权限");
        }

        // 移除所有加入队伍的关联信息
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("teamId", teamId);
        boolean result = userTeamService.remove(userTeamQueryWrapper);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除队伍关联信息失败");
        }
        // 删除队伍
        return this.removeById(teamId);
    }

    /**
     * 根据 id 获取队伍信息
     *
     * @param teamId
     * @return
     */
    public Team getTeamById(Long teamId) {
        if (teamId == null || teamId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //队伍必须存在
        Team team = this.getById(teamId);
        if (team == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "队伍不存在");
        }
        return team;
    }

    private List<TeamVO> getTeamVOList(List<Team> teamList, User loginUserInfo) {
        List<Long> teamIds = CollStreamUtil.toList(teamList, Team::getId);
        UserTeam userTeam = new UserTeam();
        userTeam.setTeamIds(teamIds);
        List<UserTeam> userTeamInfo = userTeamService.getUserTeamInfo(userTeam);
        Map<Long, List<UserTeam>> groupUserTeam = new HashMap<>();
        if (CollectionUtil.isNotEmpty(userTeamInfo)) {
            groupUserTeam = userTeamInfo.stream().collect(Collectors.groupingBy(UserTeam::getTeamId));
        }
        Map<Long, List<UserTeam>> finalGroupUserTeam = groupUserTeam;

        // 查询自己加入的队伍
        LambdaQueryWrapper<UserTeam> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserTeam::getUserId, loginUserInfo.getId());
        List<UserTeam> myjoinTeamList = userTeamService.list(lambdaQueryWrapper);
        List<Long> myJoinTeamIds = CollStreamUtil.toList(myjoinTeamList, UserTeam::getTeamId);
        List<TeamVO> teamVOList = teamList.stream().map(team1 -> this.converToTeamVo(team1, finalGroupUserTeam, myJoinTeamIds)).collect(Collectors.toList());
        return teamVOList;
    }

    private TeamVO converToTeamVo(Team team, Map<Long, List<UserTeam>> mapUserTeam, List<Long> teamIds) {
        TeamVO teamVO = new TeamVO();
        BeanUtils.copyProperties(team, teamVO);
        Long teamId = team.getId();
        List<UserTeam> userTeams = Optional.ofNullable(mapUserTeam.get(teamId)).orElse(new ArrayList<>());
        teamVO.setHasJoinNum(userTeams.size());
        if (!teamIds.contains(teamId)) {
            return teamVO;
        }
        teamVO.setHasJoin(true);
        return teamVO;
    }

}




