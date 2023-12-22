package com.study.shortLink.project.controller;

import com.study.shortLink.project.common.convention.result.Result;
import com.study.shortLink.project.common.convention.result.Results;
import com.study.shortLink.project.dto.req.ShortLinkCreateReqDTO;
import com.study.shortLink.project.dto.resp.ShortLinkCreateRespDTO;
import com.study.shortLink.project.service.ShortLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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

}
