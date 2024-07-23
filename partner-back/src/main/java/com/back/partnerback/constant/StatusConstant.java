package com.back.partnerback.constant;


/**  状态常量
 * @author yang
 * @create 2023-07-10 21:47
 */
public interface StatusConstant {


    /**
     *  未删除
     */
    Integer USER_DELETE_NOT_DELETE  = 0;

    /**
     *  删除
     */
    Integer USER_DELETE_IS_DELETE  = 1;

    /**
     * 盐值，使密码更加复杂
     */
    String USER_PASSWORD_KEY = "yang";

    /**
     * 用户登录态
     */
    String USER_LOGIN_STATE = "userLoginState_";

    /**
     * 管理员角色
     */
    String ADMIN_ROLE = "1";

    /**
     *  普通角色
     */
    String  ORDINARY_ROLE = "0";


    Integer TEAM_SECRET = 2; // 加密

    Integer TEAM_PRIVATE = 1; // 私密

    Integer TEAM_PUBLIC = 0; // 公开
}
