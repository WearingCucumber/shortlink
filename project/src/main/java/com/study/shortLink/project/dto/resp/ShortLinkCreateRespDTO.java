package com.study.shortLink.project.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 短链接返回实体
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShortLinkCreateRespDTO {
    /**
     * 分组标识
     */
    private String gid;
    /**
     * 完整短链接
     */
    private String fullShortUrl;

    /**
     * 原始链接
     */
    private String originUrl;



}
