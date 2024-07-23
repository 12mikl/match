package com.back.partnerback.model.Request;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: 楼兰
 * @CreateTime: 2024-07-20
 * @Description:
 */
@Data
public class UserJoinTeamRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 密码
     */
    private String password;

    private static final long serialVersionUID = 1L;
}
