package com.back.partnerback.service.impl;

import com.back.partnerback.mapper.UserTeamMapper;
import com.back.partnerback.model.UserTeam;
import com.back.partnerback.service.UserService;
import com.back.partnerback.service.UserTeamService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
* @author user
* @description 针对表【user_team(用户队伍关系)】的数据库操作Service实现
* @createDate 2024-07-17 11:30:21
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService {

    @Override
    public List<UserTeam> getUserTeamInfo(UserTeam userTeam) {
        LambdaQueryWrapper<UserTeam> queryWrapper = new LambdaQueryWrapper<>();
        if (ObjectUtils.isNotEmpty(userTeam.getUserId())) {
            queryWrapper.eq(UserTeam::getUserId,userTeam.getUserId());
        }
        if (ObjectUtils.isNotEmpty(userTeam.getTeamId())) {
            queryWrapper.eq(UserTeam::getTeamId,userTeam.getTeamId());
        }
        if (ObjectUtils.isNotEmpty(userTeam.getTeamIds())) {
            queryWrapper.in(UserTeam::getTeamId,userTeam.getTeamIds());
        }
        List<UserTeam> userTeams = this.baseMapper.selectList(queryWrapper);
        return userTeams;
    }


}




