package com.study.shortLink.admin.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.study.shortLink.admin.common.convention.result.Result;
import com.study.shortLink.admin.remote.ShortLinkStatsRemoteService;
import com.study.shortLink.admin.remote.dto.req.ShortLinkStatsAccessRecordReqDTO;
import com.study.shortLink.admin.remote.dto.req.ShortLinkStatsReqDTO;
import com.study.shortLink.admin.remote.dto.resp.ShortLinkStatsAccessRecordRespDTO;
import com.study.shortLink.admin.remote.dto.resp.ShortLinkStatsRespDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 短链接监控控制层
 */
@RestController
public class ShortLinkStatsController {

    private final ShortLinkStatsRemoteService shortLinkStatsRemoteService  = new ShortLinkStatsRemoteService() {
    };

    /**
     * 访问单个短链接指定时间内监控数据
     */
    @GetMapping("/api/short-link/admin/v1/stats")
    public Result<ShortLinkStatsRespDTO> shortLinkStats(ShortLinkStatsReqDTO requestParam) {
        return shortLinkStatsRemoteService.oneShortLinkStats(requestParam);
    }
    /**
     * 访问单个短链接指定时间内访问记录
     */
    @GetMapping("/api/short-link/admin/v1/stats/access-record")
    public Result<IPage<ShortLinkStatsAccessRecordRespDTO>> shortLinkStatsAccessRecord(ShortLinkStatsAccessRecordReqDTO requestParam) {
        return shortLinkStatsRemoteService.shortLinkStatsAccessRecord(requestParam);
    }
}