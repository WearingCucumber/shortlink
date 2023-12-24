package com.study.shortLink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.jwt.JWT;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.study.shortLink.admin.common.biz.user.UserContext;
import com.study.shortLink.admin.common.convention.exception.ClientException;
import com.study.shortLink.admin.dao.entity.UserDO;
import com.study.shortLink.admin.dao.mapper.UserMapper;
import com.study.shortLink.admin.dto.req.UserLoginReqDTO;
import com.study.shortLink.admin.dto.req.UserRegisterReqDTO;
import com.study.shortLink.admin.dto.req.UserUpdateReqDTO;
import com.study.shortLink.admin.dto.resp.UserLoginRespDTO;
import com.study.shortLink.admin.dto.resp.UserRespDTO;
import com.study.shortLink.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static com.study.shortLink.admin.common.constant.RedisCacheConstant.LOCK_USER_REGISTER_KEY;
import static com.study.shortLink.admin.common.enums.UserErrorCodeEnum.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {
    private final RBloomFilter<String> userRegisterCachePenetrationBloomFilter;
    private final RedissonClient redissonClient;
    private final StringRedisTemplate stringRedisTemplate;

    public UserRespDTO getUserByUsername(String username) {
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, username);
        UserDO userDO = baseMapper.selectOne(queryWrapper);
        if (userDO == null)
            throw new ClientException(USER_NULL);
        UserRespDTO userRespDTO = new UserRespDTO();
        BeanUtil.copyProperties(userDO, userRespDTO);

        return userRespDTO;
    }

    @Override
    public Boolean hasUsername(String username) {
        return !userRegisterCachePenetrationBloomFilter.contains(username);
    }

    @Override
    public void register(UserRegisterReqDTO requestParam) {

        if (!hasUsername(requestParam.getUsername())) {
            throw new ClientException(USER_NAME_EXIST);
        }
        RLock lock = redissonClient.getLock(LOCK_USER_REGISTER_KEY + requestParam.getUsername());
        try {
            if (lock.tryLock()) {
                int insert;
                try {
                    insert = baseMapper.insert(BeanUtil.toBean(requestParam, UserDO.class));
                    if (insert < 1) {
                        throw new ClientException(USER_SAVE_ERROR);
                    }
                } catch (DuplicateKeyException exception) {
                    throw new ClientException(USER_EXIST);
                }
                userRegisterCachePenetrationBloomFilter.add(requestParam.getUsername());
                return;
            }
            throw new ClientException(USER_NAME_EXIST);
        } finally {
            lock.unlock();
        }

    }

    @Override
    public void update(UserUpdateReqDTO requestParam) {

        String username = UserContext.getUsername();
        if (username.equals(requestParam.getUsername())) {
            LambdaUpdateWrapper<UserDO> wrapper = Wrappers.lambdaUpdate(UserDO.class).eq(UserDO::getUsername, requestParam.getUsername());
            baseMapper.update(BeanUtil.toBean(requestParam, UserDO.class), wrapper);
        } else {
            throw new ClientException("登录用户与当前用户不一致");
        }
    }

    @Override
    public UserLoginRespDTO login(UserLoginReqDTO requestParam) {
        Boolean hasLogin = stringRedisTemplate.hasKey("login_" + requestParam.getUsername());
        if (hasLogin != null && hasLogin) {
            throw new ClientException("用户已登录");
        }
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, requestParam.getUsername())
                .eq(UserDO::getPassword, requestParam.getPassword())
                .eq(UserDO::getDelFlag, 0);
        UserDO userDO = baseMapper.selectOne(queryWrapper);
        if (userDO == null) {
            throw new ClientException("用户不存在");
        }

        /**
         * Hash
         * Key: login_用户名
         * Value:
         *  Key: token 标识
         *  val：json 字符串（用户信息）
         */

        String token = JWT.create()
                .setPayload("username", requestParam.getUsername())
                .setKey("WearingCucumber".getBytes())
                .sign();
//        String uuid = UUID.randomUUID().toString();
        stringRedisTemplate.opsForHash().put("login_" + requestParam.getUsername(), token, JSON.toJSONString(userDO));
        stringRedisTemplate.expire("login_" + requestParam.getUsername(), 30, TimeUnit.DAYS);
        return new UserLoginRespDTO(token);
    }

    @Override
    public Boolean checkLogin(String username, String token) {
        return stringRedisTemplate.opsForHash().get("login_" + username, token) != null;

    }

    @Override
    public void logout() {
        String token = UserContext.getToken();
        String username = UserContext.getUsername();
        if (checkLogin(username, token)) {
            stringRedisTemplate.delete("login_" + username);
            return;
        }
        throw new ClientException("用户token不存在或用户未登录");
    }
}
