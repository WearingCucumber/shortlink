package com.study.shortLink.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.study.shortLink.admin.common.convention.result.Result;
import com.study.shortLink.admin.common.convention.result.Results;
import com.study.shortLink.admin.remote.RecycleBinRemoteService;
import com.study.shortLink.admin.remote.dto.req.RecycleBinPageReqDTO;
import com.study.shortLink.admin.remote.dto.req.RecycleBinSaveReqDTO;
import com.study.shortLink.admin.remote.dto.resp.ShortLinkPageRespDTO;
import com.study.shortLink.admin.service.RecycleBinService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 回收站控制层
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class RecycleBinController {
    private final RecycleBinService recycleBinService;
    RecycleBinRemoteService recycleBinRemoteService = new RecycleBinRemoteService(){};

    /**
     * 短链接添加到回收站中
     * @param requestParam
     * @return
     */
    @PostMapping("/api/short-link/admin/v1/recycle-bin/save")
    public Result<Void> saveRecycleBin(@RequestBody RecycleBinSaveReqDTO requestParam){

        recycleBinRemoteService.saveRecycleBin(requestParam);
        return Results.success();
    }
    /**
     * 分页查询
     * @param requestParam
     * @return
     */
    @GetMapping("/api/short-link/admin/v1/recycle-bin/page")
    public Result<IPage<ShortLinkPageRespDTO>> page(RecycleBinPageReqDTO requestParam){
        return  recycleBinService.recycleBinPage(requestParam);
    }
}
