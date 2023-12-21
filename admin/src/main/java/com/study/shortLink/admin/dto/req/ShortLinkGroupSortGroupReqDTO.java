package com.study.shortLink.admin.dto.req;

import lombok.Data;

/**
 * 短链接分组排序参数
 */
@Data
public class ShortLinkGroupSortGroupReqDTO {
    /**
     * 分组id
     */
    private String gid;
    /**
     * 排序
     */
    private Integer sortOrder;
}
