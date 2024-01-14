package com.study.shortLink.project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.study.shortLink.project.common.convention.result.Result;
import com.study.shortLink.project.common.convention.result.Results;
import com.study.shortLink.project.dto.req.RecycleBinPageReqDTO;
import com.study.shortLink.project.dto.req.RecycleBinRecoverReqDTO;
import com.study.shortLink.project.dto.req.RecycleBinRemoveReqDTO;
import com.study.shortLink.project.dto.req.RecycleBinSaveReqDTO;
import com.study.shortLink.project.dto.resp.ShortLinkPageRespDTO;
import com.study.shortLink.project.service.RecycleBinService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 回收站控制层
 */
@RestController
@RequiredArgsConstructor
public class RecycleBinController {
    private final RecycleBinService recycleBinService;

    /**
     * 短链接添加到回收站中
     * @param requestParam
     * @return
     */
    @PostMapping("/api/short-link/v1/recycle-bin/save")
    public Result<Void> saveRecycleBin(@RequestBody RecycleBinSaveReqDTO requestParam){
        recycleBinService.saveRecycleBin(requestParam);
        return Results.success();
    }

    /**
     * 分页查询
     * @param requestParam
     * @return
     */
    @GetMapping("/api/short-link/v1/recycle-bin/page")
    public Result<IPage<ShortLinkPageRespDTO>> page(RecycleBinPageReqDTO requestParam){
        return  Results.success(recycleBinService.recycleBinPage(requestParam));
    }

    /**
     * 恢复短链接
     * @param requestParam
     * @return
     */
    @PostMapping("/api/short-link/v1/recycle-bin/recover")
    public Result<Void> recoverRecycleBin(@RequestBody RecycleBinRecoverReqDTO requestParam){
        recycleBinService.recoverRecycleBin(requestParam);
        return Results.success();
    }

    /**
     * 回收站删除短链接
     * @param requestParam
     * @return
     */
    @PostMapping("/api/short-link/v1/recycle-bin/remove")
    public Result<Void> delete (@RequestBody RecycleBinRemoveReqDTO requestParam){
        recycleBinService.remove(requestParam);
        return Results.success();
    }
}
