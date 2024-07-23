package com.back.partnerback.model.Dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author yang
 * @create 2024-05-12 18:09
 */
@Data
public class QueryUserDTO implements Serializable {

    /**
     * 昵称
     */
    private String userName;

    /**
     * 登录账号
     */
    private String userAccount;

    /**
     * 性别
     */
    private Integer userGender;

    /**
     * 电话
     */
    private String userPhone;

    /**
     * 邮箱
     */
    private String userEmail;

    /**
     * 角色权限 0用户 1管理员
     */
    private Integer userRole;

    /**
     * 是否删除 0 1（逻辑删除）
     */
    private Integer isDelete;

}
