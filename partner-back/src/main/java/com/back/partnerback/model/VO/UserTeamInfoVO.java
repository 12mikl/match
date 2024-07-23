package com.back.partnerback.model.VO;

import com.back.partnerback.model.User;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: 楼兰
 * @CreateTime: 2024-07-19
 * @Description:
 */
@Data
public class UserTeamInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 队伍名称
     */
    private String name;

    /**
     * 最大人数
     */
    private Integer maxNum;

    /**
     *  加入队伍用户信息
     */
    private List<User> addTeamUserInfos;

}
