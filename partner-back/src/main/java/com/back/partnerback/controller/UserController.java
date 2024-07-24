package com.back.partnerback.controller;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.TypeReference;
import com.back.partnerback.Exception.BusinessException;
import com.back.partnerback.common.BaseResponse;
import com.back.partnerback.common.ErrorCode;
import com.back.partnerback.common.ResultUtils;
import com.back.partnerback.constant.StatusConstant;
import com.back.partnerback.model.Dto.QueryUserDTO;
import com.back.partnerback.model.Request.QueryPageRequest;
import com.back.partnerback.model.Request.UserLoginRequest;
import com.back.partnerback.model.Request.UserRegisterRequest;
import com.back.partnerback.model.User;
import com.back.partnerback.service.UserService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author yang
 * @create 2023-07-08 16:28
 */
@RestController
@Api(tags = "用户接口")
@RequestMapping("user")
public class UserController {

    @Resource
    private UserService userService;

    private ExecutorService executorService = new ThreadPoolExecutor(60,1000,10000, TimeUnit.MINUTES,new ArrayBlockingQueue<>(10000));

    @ApiOperation("注册")
    @PostMapping("register")
    public BaseResponse<Boolean> userRegister(@RequestBody UserRegisterRequest userRegisterRequest){
        User user = new User();
        BeanUtils.copyProperties(userRegisterRequest,user);
        long l = userService.userRegister(user);
        if (l < 0) {
            return ResultUtils.success(false);
        }
        return ResultUtils.success(true);
    }

    @ApiOperation("登陆")
    @PostMapping("login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request){
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        User resDTO = userService.userLogin(userAccount,userPassword,request);
        return ResultUtils.success(resDTO);
    }

    @ApiOperation("退出")
    @PostMapping("quit")
    public BaseResponse<Boolean> userQuit(HttpServletRequest request){
        boolean result = this.userService.userQuit(request);
        return ResultUtils.success(result);
    }

    @ApiOperation("登录后获取当前用户信息")
    @GetMapping("currentUser")
    public  BaseResponse<User> getCurrentUserInfo(HttpServletRequest request){
        User userLoginResDTO = userService.getLoginUserInfo(request);
        return ResultUtils.success(userLoginResDTO);
    }


//    @RequireRoles(StatusConstant.ADMIN_ROLE)
    @ApiOperation("查询用户列表")
    @GetMapping("queryUserList")
    public BaseResponse<List<User>> queryUserList(QueryPageRequest pageRequest, QueryUserDTO queryUserDTO, HttpServletRequest request){
        User user = this.judgeUser(request);
        if (ObjectUtils.isEmpty(user) ) {
            throw new BusinessException(ErrorCode.NO_LOGIN);
        }
        if (!user.getUserRole().equals(StatusConstant.ADMIN_ROLE)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        IPage<User> page = userService.queryUserList(pageRequest, queryUserDTO);
        return ResultUtils.success(page.getRecords());
    }

//    @RequireRoles(StatusConstant.ADMIN_ROLE)
    @ApiOperation("删除用户")
    @PostMapping("deleteUserById")
    public BaseResponse<Boolean> deleteUserById(long id,HttpServletRequest request){
        User user = this.judgeUser(request);
        if (ObjectUtils.isEmpty(user) || !user.getUserRole().equals(StatusConstant.ADMIN_ROLE)) {
            return ResultUtils.success(false);
        }
        userService.deleteUserById(id);
        return ResultUtils.success(true);
    }

    @ApiOperation("根据Id查询当前用户信息")
    @GetMapping("getCurrentUserInfo")
    public BaseResponse<User> getCurrentUserInfoById(Integer id){
        User user = userService.getById(id);
        user.setUserPassword(null);
        return ResultUtils.success(user);
    }

    @ApiOperation("修改用户信息")
    @PostMapping("updateUserInfoById")
    public BaseResponse<Integer> updateUserInfoById(@RequestBody User user){
        return ResultUtils.success(userService.updateUserInfo(user));
    }

    public User judgeUser(HttpServletRequest request){
        Object attribute = request.getSession().getAttribute(StatusConstant.USER_LOGIN_STATE);
        User user = Convert.convert(new TypeReference<User>() {
        }, attribute);
        return user;
    }

    @ApiOperation("根据标签匹配用户")
    @GetMapping("matchUserByTag")
    public BaseResponse<List<User>> matchUserByTag(String jsonTags){
        if (StringUtils.isBlank(jsonTags)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<String> tags = Arrays.stream(jsonTags.split(","))
                .map(String::trim).collect(Collectors.toList());

        List<User> userLoginResDTOList = userService.matchUserByTagName(tags);
        return ResultUtils.success(userLoginResDTOList);
    }

    @ApiOperation("用户推荐")
    @GetMapping("/recommend")
    public BaseResponse<IPage<User>> recommendUsers(QueryPageRequest pageRequest,HttpServletRequest request) {
        IPage<User> userIPage = userService.recommendUsers(pageRequest,request);
        return ResultUtils.success(userIPage);
    }

    @ApiOperation("匹配相似的用户")
    @GetMapping("/match")
    public BaseResponse<List<User>> matchUsers(int num,HttpServletRequest request){
        if (num <=0 || num >20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<User> userList = userService.matchUsers(num, request);
        return ResultUtils.success(userList);
    }
}
