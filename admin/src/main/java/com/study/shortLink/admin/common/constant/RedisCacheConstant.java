package com.study.shortLink.admin.common.constant;

import org.springframework.stereotype.Component;

/**
 * 短链接后管 Redis缓存常量类
 */
@Component
public class RedisCacheConstant {
    public static final String LOCK_USER_REGISTER_KEY = "short-link:lock_user-register:";
}
