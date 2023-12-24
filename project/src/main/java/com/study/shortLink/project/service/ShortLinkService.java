package com.study.shortLink.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.study.shortLink.project.dao.entity.ShortLinkDO;
import com.study.shortLink.project.dto.req.ShortLinkCreateReqDTO;
import com.study.shortLink.project.dto.req.ShortLinkPageReqDTO;
import com.study.shortLink.project.dto.resp.ShortLinkCreateRespDTO;
import com.study.shortLink.project.dto.resp.ShortLinkGroupCountQueryRespDTO;
import com.study.shortLink.project.dto.resp.ShortLinkPageRespDTO;

import java.util.List;

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

    /**
     * 短链接分页查询
     * @param requestParam 短链接分页查询请求参数
     * @return
     */
    IPage<ShortLinkPageRespDTO> shortLinkPage(ShortLinkPageReqDTO requestParam);

    /**
     * 根据gid组查询组内短链接数量
     * @param requestParam
     * @return
     */

    List<ShortLinkGroupCountQueryRespDTO> listGroupShortLinkCount(List<String> requestParam);
}
