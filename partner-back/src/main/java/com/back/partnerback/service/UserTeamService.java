package com.back.partnerback.service;

import com.back.partnerback.model.UserTeam;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author user
* @description 针对表【user_team(用户队伍关系)】的数据库操作Service
* @createDate 2024-07-17 11:30:21
*/
public interface UserTeamService extends IService<UserTeam> {

    /**
     *  获取用户队伍信息
     * @param userTeam
     * @return
     */
    List<UserTeam> getUserTeamInfo(UserTeam userTeam);

}
