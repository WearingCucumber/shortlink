package com.study.shortLink.project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.study.shortLink.project.common.convention.result.Result;
import com.study.shortLink.project.common.convention.result.Results;
import com.study.shortLink.project.dto.req.ShortLinkCreateReqDTO;
import com.study.shortLink.project.dto.req.ShortLinkPageReqDTO;
import com.study.shortLink.project.dto.resp.ShortLinkCreateRespDTO;
import com.study.shortLink.project.dto.resp.ShortLinkPageRespDTO;
import com.study.shortLink.project.service.ShortLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 短连接控制层
 */
@RequiredArgsConstructor
@RestController
public class ShortLinkController {
    private final ShortLinkService shortLinkService;

    /**
     * 创建短链接
     */
    @PostMapping("/api/short-link/v1/create")
    public Result<ShortLinkCreateRespDTO> createShortLink(@RequestBody ShortLinkCreateReqDTO requestParam){
        ShortLinkCreateRespDTO result = shortLinkService.createShortLink(requestParam);
        return Results.success(result);
    }
    @GetMapping("/api/short-link/v1/page")
    public Result<IPage<ShortLinkPageRespDTO>> page(ShortLinkPageReqDTO requestParam){
        return  Results.success(shortLinkService.shortLinkPage(requestParam));
    }

}
