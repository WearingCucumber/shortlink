package com.study.shortLink.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.study.shortLink.project.dao.entity.ShortLinkDO;
import com.study.shortLink.project.dto.req.ShortLinkCreateReqDTO;
import com.study.shortLink.project.dto.resp.ShortLinkCreateRespDTO;

/**
 * 短连接接口层
 */
public interface ShortLinkService extends IService<ShortLinkDO> {
    /**
     * 创建短链接
     * @param requestParam 短链接创建信息
     * @return
     */
    ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam);
}
