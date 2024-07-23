package com.back.partnerback.service;

import com.back.partnerback.model.Dto.TeamAddDTO;
import com.back.partnerback.model.Dto.TeamQueryDTO;
import com.back.partnerback.model.Request.UpdateTeamRequest;
import com.back.partnerback.model.Request.UserJoinTeamRequest;
import com.back.partnerback.model.Team;
import com.back.partnerback.model.VO.TeamVO;
import com.back.partnerback.model.VO.UserTeamInfoVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author user
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2024-07-17 11:29:37
*/
public interface TeamService extends IService<Team> {

    /**
     *  获取队伍信息列表
     * @param team
     * @return
     */
    List<Team> getTeamList(Team team);

    /**
     *  新增队伍
     * @param teamAddDTO
     * @param request
     * @return
     */
    long addTeam(TeamAddDTO teamAddDTO,HttpServletRequest request);

    /**
     * 我创建的队伍
     * @param name
     * @return
     */
    List<TeamVO> myCreateTeam(String name,HttpServletRequest request);

    /**
     * 我加入的队伍
     * @param name
     * @param request
     * @return
     */
    List<TeamVO> myJoinTeam(String name,HttpServletRequest request);

    /**
     * 查询公开或加密的队伍
     * @param queryDTO
     * @return
     */
    IPage<TeamVO> getPublicOrSecretTeams(TeamQueryDTO queryDTO,HttpServletRequest request);

    /**
     *  根据队伍id查询队伍
     * @param id
     * @return
     */
    UserTeamInfoVO getUserTeamInfoById(Long id);

    /**
     *  修改队伍信息
     * @param updateTeamRequest
     * @param request
     * @return
     */
    boolean updateTeam(UpdateTeamRequest updateTeamRequest, HttpServletRequest request);

    /**
     *  用户加入队伍
     * @param userJoinTeamRequest 队伍
     * @return
     */
    boolean joinUserTeam(UserJoinTeamRequest userJoinTeamRequest, HttpServletRequest request);

    /**
     *  退出队伍
     * @param id
     * @param request
     */
    boolean quitTeam(Long id, HttpServletRequest request);

    /**
     *  解散队伍
     * @param id
     * @param request
     * @return
     */
    boolean deleteTeam(long id, HttpServletRequest request);

}
