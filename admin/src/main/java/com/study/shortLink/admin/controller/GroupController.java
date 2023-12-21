package com.study.shortLink.admin.controller;

import com.study.shortLink.admin.common.convention.result.Result;
import com.study.shortLink.admin.common.convention.result.Results;
import com.study.shortLink.admin.dto.req.ShortLinkGroupSaveGroupReqDTO;
import com.study.shortLink.admin.dto.req.ShortLinkGroupUpdateGroupReqDTO;
import com.study.shortLink.admin.dto.resp.ShortLinkGroupRespDTO;
import com.study.shortLink.admin.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分组管理控制层
 */
@RestController
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;

    /**
     * 新增短链接分组
     * @param requestParam 组名称
     * @return
     */
    @PostMapping("/api/short-link/v1/group")
    public Result<Void> saveGroup(@RequestBody ShortLinkGroupSaveGroupReqDTO requestParam){
        groupService.saveGroup(requestParam.getName());
        return Results.success();
    }
    @GetMapping("/api/short-link/v1/group")
    public Result<List<ShortLinkGroupRespDTO>> listGroup(){
        return Results.success(groupService.listGroup());
    }

    /**
     * 修改短链接分组名称
     * @param requestParam 组名称
     * @return
     */
    @PutMapping("/api/short-link/v1/group")
    public Result<Void> updateGroup(@RequestBody ShortLinkGroupUpdateGroupReqDTO requestParam){
        groupService.updateGroup(requestParam);
        return Results.success();
    }
}
