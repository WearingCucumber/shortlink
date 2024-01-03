package com.study.shortLink.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.study.shortLink.admin.common.convention.result.Result;
import com.study.shortLink.admin.remote.ShortLinkRemoteService;
import com.study.shortLink.admin.remote.dto.ShortLinkGroupCountQueryRespDTO;
import com.study.shortLink.admin.remote.dto.req.ShortLinkCreateReqDTO;
import com.study.shortLink.admin.remote.dto.req.ShortLinkPageReqDTO;
import com.study.shortLink.admin.remote.dto.resp.ShortLinkCreateRespDTO;
import com.study.shortLink.admin.remote.dto.resp.ShortLinkPageRespDTO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 短链接后管管理控制层
 */
@RestController

public class ShortLinkController {
    ShortLinkRemoteService shortLinkRemoteService = new ShortLinkRemoteService(){};
    @GetMapping("/api/short-link/admin/v1/page")
    public Result<IPage<ShortLinkPageRespDTO>> page(ShortLinkPageReqDTO requestParam){
        return  shortLinkRemoteService.page(requestParam);
    }
    @PostMapping("/api/short-link/admin/v1/create")
    public Result<ShortLinkCreateRespDTO> createShortLink(@RequestBody ShortLinkCreateReqDTO requestParam){
        return shortLinkRemoteService.createShortLink(requestParam);
    }
    @GetMapping("/api/short-link/admin/v1/count")
    public Result<List<ShortLinkGroupCountQueryRespDTO>> listGroupShortLinkCount(@RequestParam("requestParam") List<String> requestParam){
        return shortLinkRemoteService.listGroupShortLinkCount(requestParam);
    }
}
