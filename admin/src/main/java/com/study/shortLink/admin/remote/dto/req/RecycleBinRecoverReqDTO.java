package com.study.shortLink.admin.remote.dto.req;

import lombok.Data;

/**
 * 回收站移除功能入参
 */
@Data
public class RecycleBinRecoverReqDTO {
    /**
     * 分组标识
     */
    private String gid;
    /**
     * 完全短链接
     */
    private String fullShortUrl;
}
