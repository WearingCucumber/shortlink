package com.study.shortLink.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.study.shortLink.admin.common.convention.result.Result;
import com.study.shortLink.admin.remote.dto.req.RecycleBinPageReqDTO;
import com.study.shortLink.admin.remote.dto.resp.ShortLinkPageRespDTO;

/**
 * 回收站接口层
 */
public interface RecycleBinService  {

    /**
     * 分页查询
     * @param requestParam
     * @return
     */
    Result<IPage<ShortLinkPageRespDTO>> recycleBinPage(RecycleBinPageReqDTO requestParam);

}
