package com.study.shortLink.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.study.shortLink.project.dto.req.ShortLinkStatsAccessRecordReqDTO;
import com.study.shortLink.project.dto.req.ShortLinkStatsReqDTO;
import com.study.shortLink.project.dto.resp.ShortLinkStatsAccessRecordRespDTO;
import com.study.shortLink.project.dto.resp.ShortLinkStatsRespDTO;

/**
 * 短链接监控接口层
 */
public interface ShortLinkStatsService {

    /**
     * 获取单个短链接监控数据
     *
     * @param requestParam 获取短链接监控数据入参
     * @return 短链接监控数据
     */
    ShortLinkStatsRespDTO oneShortLinkStats(ShortLinkStatsReqDTO requestParam);

    /**
     * 访问单个短链接指定时间内访问记录
     *
     * @param requestParam
     * @return
     */
    IPage<ShortLinkStatsAccessRecordRespDTO> shortLinkStatsAccessRecord(ShortLinkStatsAccessRecordReqDTO requestParam);
}
