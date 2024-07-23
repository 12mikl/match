package com.back.partnerback.controller;

import com.back.partnerback.Exception.BusinessException;
import com.back.partnerback.common.BaseResponse;
import com.back.partnerback.common.ErrorCode;
import com.back.partnerback.common.ResultUtils;
import com.back.partnerback.model.Dto.TeamAddDTO;
import com.back.partnerback.model.Dto.TeamQueryDTO;
import com.back.partnerback.model.Request.UpdateTeamRequest;
import com.back.partnerback.model.Request.UserJoinTeamRequest;
import com.back.partnerback.model.Team;
import com.back.partnerback.model.VO.TeamVO;
import com.back.partnerback.model.VO.UserTeamInfoVO;
import com.back.partnerback.service.TeamService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author yang
 * @create 2023-07-08 16:28
 */
@RestController
@Api(tags = "队伍接口")
@RequestMapping("team")
public class TeamController {

    @Resource
    private TeamService teamService;
    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @ApiOperation("新增队伍")
    @PostMapping("/add")
    public BaseResponse<Long> addTeam(@RequestBody TeamAddDTO teamAddDTO,HttpServletRequest request){
        long teamId = teamService.addTeam(teamAddDTO,request);
        return ResultUtils.success(teamId);
    }

    @ApiOperation("我创建的队伍")
    @GetMapping("/list/my/create")
    public BaseResponse<List<TeamVO>> myCreate(String searchText, HttpServletRequest request){
        List<TeamVO> teams = teamService.myCreateTeam(searchText,request);
        return ResultUtils.success(teams);
    }

    @ApiOperation("我加入的队伍")
    @GetMapping("/list/my/join")
    public BaseResponse<List<TeamVO>> myJoin(String searchText, HttpServletRequest request){
        List<TeamVO> teams = teamService.myJoinTeam(searchText,request);
        return ResultUtils.success(teams);
    }

    @ApiOperation("分页查询队伍")
    @GetMapping("/list")
    public BaseResponse<IPage<TeamVO>> getTeamsByPage(TeamQueryDTO queryDTO,HttpServletRequest request){
        if (ObjectUtils.isEmpty(queryDTO)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        IPage<TeamVO> publicOrSecretTeams = teamService.getPublicOrSecretTeams(queryDTO,request);
        return ResultUtils.success(publicOrSecretTeams);
    }

    @ApiOperation("查询队伍人员信息")
    @GetMapping("/userTeamInfo/{id}")
    public BaseResponse<UserTeamInfoVO> getUserTeamInfo(@PathVariable Long id){
        if (ObjectUtils.isEmpty(id)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserTeamInfoVO userTeamInfoById = teamService.getUserTeamInfoById(id);
        return ResultUtils.success(userTeamInfoById);
    }

    @ApiOperation("修改队伍")
    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeamById(@RequestBody UpdateTeamRequest updateTeamRequest,HttpServletRequest request){
        if (ObjectUtils.isEmpty(updateTeamRequest)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean updateResult = teamService.updateTeam(updateTeamRequest,request);
        if (!updateResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"修改失败");
        }
        return ResultUtils.success(true);
    }

    @ApiOperation("查询单个队伍")
    @GetMapping("/getById")
    public BaseResponse<Team> getTeamById(@RequestParam("id") Long id){
        if (ObjectUtils.isEmpty(id)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team getResult = teamService.getById(id);
        if (getResult == null) {
            throw new BusinessException(ErrorCode.NULL_PARAMS);
        }
        return ResultUtils.success(getResult);
    }

    @ApiOperation("加入队伍")
    @PostMapping("/join")
    public BaseResponse<Boolean> joinTeam(@RequestBody UserJoinTeamRequest userJoinTeamRequest, HttpServletRequest request){
        boolean result = teamService.joinUserTeam(userJoinTeamRequest,request);
        return ResultUtils.success(result);
    }

    @ApiOperation("退出队伍")
    @PostMapping("/quit")
    public BaseResponse<Boolean> quitTeam(@RequestBody UserJoinTeamRequest userJoinTeamRequest,HttpServletRequest request){
        boolean result = teamService.quitTeam(userJoinTeamRequest.getId(), request);
        return ResultUtils.success(result);
    }

    @ApiOperation("解散队伍")
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeamById(@RequestBody UserJoinTeamRequest userJoinTeamRequest,HttpServletRequest request){
        if (userJoinTeamRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean deleteResult = teamService.deleteTeam(userJoinTeamRequest.getId(),request);
        if (!deleteResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"删除失败");
        }
        return ResultUtils.success(true);
    }
}
