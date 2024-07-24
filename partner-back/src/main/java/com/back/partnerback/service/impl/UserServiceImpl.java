package com.back.partnerback.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.lang.TypeReference;
import com.back.partnerback.Exception.BusinessException;
import com.back.partnerback.common.ErrorCode;
import com.back.partnerback.constant.StatusConstant;
import com.back.partnerback.mapper.UserMapper;
import com.back.partnerback.model.Dto.QueryUserDTO;
import com.back.partnerback.model.Request.QueryPageRequest;
import com.back.partnerback.model.User;
import com.back.partnerback.service.UserService;
import com.back.partnerback.utils.AlgorithmUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 *
 */


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private UserMapper userMapper;
    //    @Autowired
//    private PasswordEncoder encoder;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public long userRegister(User user) {
        // 检查账号、密码、校验密码不为空
        if (StringUtils.isAnyBlank(user.getUserAccount(), user.getUserPassword(), user.getCheckPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 检验有效和未被删除的账号不能重复
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserAccount, user.getUserAccount());
        queryWrapper.eq(User::getIsDelete, StatusConstant.USER_DELETE_NOT_DELETE);
        long count = this.count(queryWrapper);
        if (count >= 1) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号不能重复");
        }

        // 账号长度不小于4位
        if (user.getUserAccount().length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号长度小于4位");
        }
        // 账号长度不大于8位
        if (user.getUserAccount().length() > 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号长度大于8位");
        }
        // 密码长度不小于8位
        if (user.getUserPassword().length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度小于8位");
        }
        // 检查密码和校验密码相同
        if (!user.getUserPassword().equals(user.getCheckPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入密码不一致");
        }
        // 密码校验 长度为8到20位,必须包含字母和数字，字母区分大小写
//        String pattern = "^(?=.*[0-9])(?=.*[a-zA-Z])(.{8,20})$";
//        boolean matches = user.getUserPassword().matches(pattern);
//        if (!matches) {
//            return -1;
//        }

        // 密码加密
        String password = DigestUtils.md5DigestAsHex((StatusConstant.USER_PASSWORD_KEY + user.getUserPassword()).getBytes());
        user.setUserPassword(password);
        user.setCreateTime(new Date());
        boolean result = this.save(user);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败");
        }
        return 1;
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
//        Object attribute = request.getSession().getAttribute(StatusConstant.USER_LOGIN_STATE + userAccount);
//        if (ObjectUtils.isNotEmpty(attribute)) {
//            return Convert.convert(new TypeReference<UserLoginResDTO>() {
//            }, attribute);
//        }
        // 检查账号、密码不为空
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.NULL_PARAMS);
        }
        // 账号长度不小于4位
//        if (userAccount.length() < 4) {
//            throw new RuntimeException("账号长度不能小于4位");
//        }
//        // 密码长度不小于8位
//        if (userPassword.length() < 8) {
//            return null;
//        }
        // 密码校验 长度为8到20位,必须包含字母和数字，字母区分大小写
//        String pattern = "^(?=.*[0-9])(?=.*[a-zA-Z])(.{8,20})$";
//        boolean matches = pattern.matches(userPassword);
//        if (!matches) {
//            return null;
//        }
        String password = DigestUtils.md5DigestAsHex((StatusConstant.USER_PASSWORD_KEY + userPassword).getBytes());
//        String encodePassword = encoder.encode(userPassword);

        // 查询有效未被删除的账号
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserAccount, userAccount);
        queryWrapper.eq(User::getUserPassword, password);
        queryWrapper.eq(User::getIsDelete, StatusConstant.USER_DELETE_NOT_DELETE);
        User user = userMapper.selectOne(queryWrapper);
        if (ObjectUtils.isEmpty(user)) {
            throw new BusinessException(ErrorCode.NO_USER);
        }
        User safeUser = getSafetyUser(user);

        // 记录用户登录状态
        request.getSession().setAttribute(StatusConstant.USER_LOGIN_STATE, safeUser);

        SimpleDateFormat simpleFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm::ss");
        log.info("用户：{}登录成功，时间：{}", userAccount, simpleFormatter.format(new Date()));
        return safeUser;
    }

    @Override
    public User getSafetyUser(User originUser) {
        if (originUser == null) {
            return null;
        }
        User safetyUser = new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setUsername(originUser.getUsername());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setUserRole(originUser.getUserRole());
        safetyUser.setIsDelete(originUser.getIsDelete());
        safetyUser.setCreateTime(originUser.getCreateTime());
        safetyUser.setTags(originUser.getTags());
        return safetyUser;
    }

    @Override
    public boolean userQuit(HttpServletRequest request) {
        if (request == null) {
            return false;
        }
        request.getSession().removeAttribute(StatusConstant.USER_LOGIN_STATE);
        SimpleDateFormat simpleFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm::ss");
        log.info("用户退出成功，时间：{}", simpleFormatter.format(new Date()));
        return true;
    }

    @Override
    public IPage<User> queryUserList(QueryPageRequest pageRequest, QueryUserDTO queryUserDTO) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNoneBlank(queryUserDTO.getUserName())) {
            queryWrapper.like(User::getUsername, queryUserDTO.getUserName());
        }
        if (StringUtils.isNoneBlank(queryUserDTO.getUserAccount())) {
            queryWrapper.like(User::getUserAccount, queryUserDTO.getUserAccount());
        }
        if (StringUtils.isNoneBlank(queryUserDTO.getUserEmail())) {
            queryWrapper.like(User::getEmail, queryUserDTO.getUserEmail());
        }
        if (StringUtils.isNoneBlank(queryUserDTO.getUserPhone())) {
            queryWrapper.like(User::getPhone, queryUserDTO.getUserPhone());
        }
        if (ObjectUtils.isNotEmpty(queryUserDTO.getIsDelete())) {
            queryWrapper.eq(User::getIsDelete, queryUserDTO.getIsDelete());
        }
        if (ObjectUtils.isNotEmpty(queryUserDTO.getUserGender())) {
            queryWrapper.eq(User::getGender, queryUserDTO.getUserGender());
        }
        if (ObjectUtils.isNotEmpty(queryUserDTO.getUserRole())) {
            queryWrapper.eq(User::getUserRole, queryUserDTO.getUserRole());
        }

        Page<User> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());
        IPage<User> userPage = this.page(page, queryWrapper);
        userPage.getRecords().stream().map(user -> {
            user.setUserPassword(null);
            return user;
        }).collect(Collectors.toList());
        return userPage;
    }

    @Override
    public int deleteUserById(long id) {
        return userMapper.deleteById(id);
    }

    @Override
    public int updateUserInfo(User user,HttpServletRequest request) {
        String userPassword = user.getUserPassword();
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        if (StringUtils.isNotBlank(userPassword)) {
            String password = DigestUtils.md5DigestAsHex((StatusConstant.USER_PASSWORD_KEY + userPassword).getBytes());
            updateWrapper.lambda().set(User::getUserPassword, password);
        }
        if (StringUtils.isNotBlank(user.getEmail())) {
            updateWrapper.lambda().set(User::getEmail, user.getEmail());
        }
        if (StringUtils.isNotBlank(user.getUsername())) {
            updateWrapper.lambda().set(User::getUsername, user.getUsername());
        }
        if (StringUtils.isNotBlank(user.getPhone())) {
            updateWrapper.lambda().set(User::getPhone, user.getPhone());
        }
        if (ObjectUtils.isNotEmpty(user.getIsDelete())) {
            updateWrapper.lambda().set(User::getIsDelete, user.getIsDelete());
        }
        if (StringUtils.isNotBlank(user.getUserRole())) {
            updateWrapper.lambda().set(User::getUserRole, user.getUserRole());
        }
        if (ObjectUtils.isNotEmpty(user.getGender())) {
            updateWrapper.lambda().set(User::getGender, user.getGender());
        }
        updateWrapper.lambda().eq(User::getId, user.getId());
        int update = userMapper.update(new User(), updateWrapper);

        User newUser = userMapper.selectById(user.getId());
        // 修改完成后更新缓存
        User safeUser = getSafetyUser(newUser);
        request.getSession().removeAttribute(StatusConstant.USER_LOGIN_STATE);
        // 记录用户登录状态
        request.getSession().setAttribute(StatusConstant.USER_LOGIN_STATE, safeUser);
        return update;
    }


    @Override
    public List<User> matchUserByTagName(List<String> tags) {
        if (ObjectUtils.isEmpty(tags)) {
            throw new BusinessException(ErrorCode.NULL_PARAMS);
        }

        // 第一种方式：使用Sql查询
//        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
//        tags.forEach(tag ->{
//            queryWrapper.like(User::getTags,tag);
//        });
//
//        List<User> userList = this.baseMapper.selectList(queryWrapper);
//        userList.stream().forEach(user ->{
//            user.setUserPassword(null);
//        });


        // 第二种方式：使用缓存处理
        // 先查询所有用户
        LambdaQueryWrapper<User> allQueryWrapper = new LambdaQueryWrapper<>();
        List<User> allUser = this.baseMapper.selectList(allQueryWrapper);

        Gson gson = new Gson();

        allUser.stream().filter(user -> {
            String userJsonTags = user.getTags();
            user.setUserPassword(null);
            Set<String> userTagsSet = gson.fromJson(userJsonTags, new TypeToken<Set<String>>() {
            }.getType());
            userTagsSet = Optional.ofNullable(userTagsSet).orElse(new HashSet<>());
            for (String tagName : userTagsSet) {
                if (!userTagsSet.contains(tagName)) {
                    return false;
                }
            }
            return true;
        }).collect(Collectors.toList());

        return allUser;
    }

    @Override
    public User getLoginUserInfo(HttpServletRequest request) {
        Object attribute = request.getSession().getAttribute(StatusConstant.USER_LOGIN_STATE);
        if (ObjectUtils.isEmpty(attribute)) {
            throw new BusinessException(ErrorCode.NO_LOGIN);
        }
        User attrUser = Convert.convert(new TypeReference<User>() {
        }, attribute);

//        Long userId = attrUser.getId();
//        User user = this.baseMapper.selectById(userId);
//        User safeUser = getSafetyUser(user);
        return attrUser;
    }

    @Override
    public List<User> getUserInfoByIds(Set<Long> ids) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(User::getId, ids);
        List<User> userList = this.baseMapper.selectList(queryWrapper);
        return userList;
    }


    @Override
    public IPage<User> recommendUsers(QueryPageRequest pageRequest, HttpServletRequest request) {
        User userInfo = this.getLoginUserInfo(request);
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        Page<User> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());

        String userKey = String.format("match:user:recommend:%s", userInfo.getId());

        IPage<User> userPage = (IPage<User>) redisTemplate.opsForValue().get(userKey);
        if (userPage != null) {
            return userPage;
        }
        userPage = this.page(page, queryWrapper);
        userPage.getRecords().stream().map(user -> {
            user.setUserPassword(null);
            return user;
        }).collect(Collectors.toList());

        try {
            redisTemplate.opsForValue().set(userKey, userPage, 30000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("set key error", e);
        }
        return userPage;
    }

    @Override
    public List<User> matchUsers(Integer num, HttpServletRequest request) {
        User loginUserInfo = getLoginUserInfo(request);
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(User::getId, User::getTags);
        queryWrapper.isNotNull(User::getTags);
        // 查询所有用户，并剔除自己
        List<User> allUser = this.list(queryWrapper).stream().filter(a -> !a.getId().equals(loginUserInfo.getId())).collect(Collectors.toList());

        Gson gson = new Gson();
        String currentUserTags = loginUserInfo.getTags();
        List<String> currentUserTagsList = gson.fromJson(currentUserTags, new TypeToken<List<String>>() {
        }.getType());
        // 当前用户的tagsJson
        List<Pair<User, Long>> pairList = new ArrayList<>();
        for (User user : allUser) {
            String matchUserTags = user.getTags();
            if (StringUtils.isBlank(matchUserTags)) {
                continue;
            }
            List<String> matchUserList = gson.fromJson(matchUserTags, new TypeToken<List<String>>() {
            }.getType());
            // 计算两个串最小距离
            long minDistance = AlgorithmUtil.minDistance(currentUserTagsList, matchUserList);
            pairList.add(new Pair<>(user, minDistance));
        }
        // 根据编辑距离从小到大排序
        List<Pair<User, Long>> topUserLists = pairList.stream()
                .sorted((a, b) -> (int) (a.getValue() - b.getValue()))
                .limit(num)
                .collect(Collectors.toList());
        List<Long> userIds = topUserLists.stream().map(pair -> pair.getKey().getId()).collect(Collectors.toList());
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.in(User::getId, userIds);
        Map<Long, List<User>> userIdUserListMap = this.list(userLambdaQueryWrapper)
                .stream()
                .map(this::getSafetyUser)
                .collect(Collectors.groupingBy(User::getId));
        List<User> userList = new ArrayList<>();
        for (Long userId : userIds) {
            if (CollectionUtils.isNotEmpty(userIdUserListMap)) {
                userList.add(userIdUserListMap.get(userId).get(0));
            }
        }
        return userList;
    }
}




