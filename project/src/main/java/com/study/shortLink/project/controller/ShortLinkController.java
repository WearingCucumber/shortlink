package com.study.shortLink.project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.study.shortLink.project.common.convention.result.Result;
import com.study.shortLink.project.common.convention.result.Results;
import com.study.shortLink.project.dto.req.ShortLinkCreateReqDTO;
import com.study.shortLink.project.dto.req.ShortLinkPageReqDTO;
import com.study.shortLink.project.dto.req.ShortLinkUpdateReqDTO;
import com.study.shortLink.project.dto.resp.ShortLinkCreateRespDTO;
import com.study.shortLink.project.dto.resp.ShortLinkGroupCountQueryRespDTO;
import com.study.shortLink.project.dto.resp.ShortLinkPageRespDTO;
import com.study.shortLink.project.service.ShortLinkService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * 短连接控制层
 */
@RequiredArgsConstructor
@RestController
public class ShortLinkController {
    private final ShortLinkService shortLinkService;


    /**
     * 短链接跳转原始链接
     * @param short_url
     * @param request
     * @param response
     * @throws IOException
     */
    @GetMapping("/{short_url}")
    public void redisRectUrl(@PathVariable("short_url")String short_url , HttpServletRequest request , HttpServletResponse response) throws IOException {
        shortLinkService.redirectUrl(short_url , request , response);
        return;
    }


    /**
     * 创建短链接
     */
    @PostMapping("/api/short-link/v1/create")
    public Result<ShortLinkCreateRespDTO> createShortLink(@RequestBody ShortLinkCreateReqDTO requestParam){
        ShortLinkCreateRespDTO result = shortLinkService.createShortLink(requestParam);
        return Results.success(result);
    }

    /**
     * 短链接修改功能
     * @param requestParam
     * @return
     */
    @PostMapping("/api/short-link/v1/update")
    public Result<Void> updateShortLink(@RequestBody ShortLinkUpdateReqDTO requestParam){
        shortLinkService.updateShortLink(requestParam);
        return Results.success();

    }

    /**
     * 分页查询
     * @param requestParam
     * @return
     */
    @GetMapping("/api/short-link/v1/page")
    public Result<IPage<ShortLinkPageRespDTO>> page(ShortLinkPageReqDTO requestParam){
        return  Results.success(shortLinkService.shortLinkPage(requestParam));
    }

    /**
     * 根据分组标识查询组内链接数量
     * @param requestParam
     * @return
     */
    @GetMapping("/api/short-link/v1/count")
    public Result<List<ShortLinkGroupCountQueryRespDTO>> listGroupShortLinkCount(@RequestParam("requestParam") List<String> requestParam){
         return Results.success(shortLinkService.listGroupShortLinkCount(requestParam));
    }

}
