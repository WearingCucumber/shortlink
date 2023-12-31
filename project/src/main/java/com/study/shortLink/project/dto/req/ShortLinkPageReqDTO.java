package com.study.shortLink.project.dto.req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.study.shortLink.project.dao.entity.ShortLinkDO;
import lombok.Data;

/**
 * 短链接分页查询入参
 */
@Data
public class ShortLinkPageReqDTO extends Page<ShortLinkDO> {
    /**
     * 分组标识
     */
    private String gid;
}