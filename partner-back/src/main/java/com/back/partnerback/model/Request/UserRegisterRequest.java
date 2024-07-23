package com.back.partnerback.model.Request;

import lombok.Data;

/** 用户注册请求
 * @author yang
 * @create 2024-05-14 23:01
 */
@Data
public class UserRegisterRequest {

    /**
     * 昵称
     */
    private String userName;

    /**
     * 登录账号
     */
    private String userAccount;

    /**
     * 密码
     */
    private String userPassword;

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
     *  检查密码
     */
    private String checkPassword;
}
