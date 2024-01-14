package com.study.shortLink.project.dto.req;

import lombok.Data;

/**
 * 回收站删除短链接功能入参
 */
@Data
public class RecycleBinRemoveReqDTO {
    /**
     * 分组标识
     */
    private String gid;
    /**
     * 完全短链接
     */
    private String fullShortUrl;
}
