package com.study.shortLink.project.mq.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 短链接监控数据存库事件
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShortLinkStatsEvent implements Serializable {
    /**
     * 短链接
     */
    private String fullShortUrl;
    /**
     * 短链接后缀
     */
    private String shortUrl;
    /**
     * 分组标识
     */
    private String gid;

    /**
     * 唯一用户标识
     */
    private AtomicReference<String> uv;

    /**
     * 是否为独立访客标识
     */
    private AtomicBoolean uvFirstFlag;
    /**
     * 请求信息
     */
    private Map<String,String> request;


}
