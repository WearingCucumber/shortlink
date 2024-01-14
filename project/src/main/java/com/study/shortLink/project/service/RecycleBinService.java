package com.study.shortLink.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.study.shortLink.project.dao.entity.ShortLinkDO;
import com.study.shortLink.project.dto.req.RecycleBinPageReqDTO;
import com.study.shortLink.project.dto.req.RecycleBinRecoverReqDTO;
import com.study.shortLink.project.dto.req.RecycleBinRemoveReqDTO;
import com.study.shortLink.project.dto.req.RecycleBinSaveReqDTO;
import com.study.shortLink.project.dto.resp.ShortLinkPageRespDTO;

/**
 * 回收站接口层
 */
public interface RecycleBinService extends IService<ShortLinkDO> {
    /**
     * 保存回收站
     * @param requestParam 请求参数
     */
    void saveRecycleBin(RecycleBinSaveReqDTO requestParam);

    /**
     * 短链接分页查询
     * @param requestParam 短链接分页查询请求参数
     * @return
     */
    IPage<ShortLinkPageRespDTO> recycleBinPage(RecycleBinPageReqDTO requestParam);
    /**
     * 恢复短链接
     * @param requestParam 请求参数
     * @return
     */
    void recoverRecycleBin(RecycleBinRecoverReqDTO requestParam);

    /**
     * 回收站删除短链接
     * @param requestParam
     * @return
     */
    void remove(RecycleBinRemoveReqDTO requestParam);
}
