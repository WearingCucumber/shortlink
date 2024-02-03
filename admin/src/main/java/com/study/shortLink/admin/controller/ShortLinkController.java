package com.study.shortLink.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.study.shortLink.admin.common.convention.result.Result;
import com.study.shortLink.admin.common.convention.result.Results;
import com.study.shortLink.admin.remote.ShortLinkActualRemoteService;
import com.study.shortLink.admin.remote.dto.req.ShortLinkCreateReqDTO;
import com.study.shortLink.admin.remote.dto.req.ShortLinkPageReqDTO;
import com.study.shortLink.admin.remote.dto.req.ShortLinkUpdateReqDTO;
import com.study.shortLink.admin.remote.dto.resp.ShortLinkCreateRespDTO;
import com.study.shortLink.admin.remote.dto.resp.ShortLinkGroupCountQueryRespDTO;
import com.study.shortLink.admin.remote.dto.resp.ShortLinkPageRespDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 短链接后管管理控制层
 */
@RestController
@RequiredArgsConstructor
public class ShortLinkController {
    private final ShortLinkActualRemoteService shortLinkActualRemoteService;


    /**
     * 短链接分页查询
     * @param requestParam
     * @return
     */
    @GetMapping("/api/short-link/admin/v1/page")
    public Result<Page<ShortLinkPageRespDTO>> page(ShortLinkPageReqDTO requestParam){
        return  shortLinkActualRemoteService.pageShortLink(requestParam.getGid(),requestParam.getCurrent(),requestParam.getSize());
    }

    /**
     * 短链接创建
     * @param requestParam
     * @return
     */
    @PostMapping("/api/short-link/admin/v1/create")
    public Result<ShortLinkCreateRespDTO> createShortLink(@RequestBody ShortLinkCreateReqDTO requestParam){
        return shortLinkActualRemoteService.createShortLink(requestParam);
    }

    /**
     * 查询分组下短链接数量
     * @param requestParam
     * @return
     */
    @GetMapping("/api/short-link/admin/v1/count")
    public Result<List<ShortLinkGroupCountQueryRespDTO>> listGroupShortLinkCount(@RequestParam("requestParam") List<String> requestParam){
        return shortLinkActualRemoteService.listGroupShortLinkCount(requestParam);
    }
    /**
     * 短链接修改功能
     * @param requestParam
     * @return
     */
    @PostMapping("/api/short-link/admin/v1/update")
    public Result<Void> updateShortLink(@RequestBody ShortLinkUpdateReqDTO requestParam){
        shortLinkActualRemoteService.updateShortLink(requestParam);
        return Results.success();

    }
}
