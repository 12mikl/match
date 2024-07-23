package com.back.partnerback.service;

import com.back.partnerback.model.Dto.QueryUserDTO;
import com.back.partnerback.model.Request.QueryPageRequest;
import com.back.partnerback.model.User;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

/**
 *
 */
public interface UserService extends IService<User> {

    /**
     *  用户注册
     * @param user
     * @return
     */
    long userRegister(User user);

    /**
     *  用户登陆
     * @param userAccount
     * @param userPassword
     * @return
     */
    User userLogin(String userAccount, String userPassword,HttpServletRequest request);

    User getSafetyUser(User originUser);

    /**
     * 用户退出系统
     * @param request
     */
    boolean userQuit(HttpServletRequest request);

    /**
     * 管理员分页查询用户信息列表
     * @param pageRequest
     * @param queryUserDTO
     * @return
     */
    IPage<User> queryUserList(QueryPageRequest pageRequest, QueryUserDTO queryUserDTO);

    /**
     * 通过Id删除用户
     * @param id
     * @return
     */
    int deleteUserById(long id);

    /**
     *  通过id修改用户信息
     * @param user
     * @return
     */
    int updateUserInfo(User user);

    /**
     *  根据标签匹配用户
     * @param tags
     * @return
     */
    List<User> matchUserByTagName(List<String> tags);


    /**
     *  登录后获取用户信息
     * @param request
     * @return
     */
    User getLoginUserInfo(HttpServletRequest request);

    /**
     *  根据ids查询用户信息
     * @param ids
     * @return
     */
    List<User> getUserInfoByIds(Set<Long> ids);


    /**
     * 推荐用户
     * @param pageRequest
     * @return
     */
    IPage<User> recommendUsers(QueryPageRequest pageRequest,HttpServletRequest request);

    /**
     *  匹配用户
     * @param num
     * @param request
     * @return
     */
    List<User> matchUsers(Integer num, HttpServletRequest request);
}
