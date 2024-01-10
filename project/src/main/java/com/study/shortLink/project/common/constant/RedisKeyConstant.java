package com.study.shortLink.project.common.constant;

/**
 * redis Key 常量类
 */
public class RedisKeyConstant {
    /**
     * 短链接跳转key 前缀
     */
    public static final String GOTO_SHORT_LINK_KEY = "short-link_goto_%s";
    /**
     * 短链接跳转所前缀
     */
    public static final String LOCK_GOTO_SHORT_LINK_KEY = "lock-short-link_goto_%s";
    /**
     * 短链接空值跳转
     */
    public static final String GOTO_IS_NULL_SHORT_LINK_KEY = "short-link_goto_is-null_%s";
}
