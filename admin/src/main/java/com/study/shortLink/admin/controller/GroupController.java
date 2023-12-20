package com.study.shortLink.admin.controller;

import com.study.shortLink.admin.common.convention.result.Result;
import com.study.shortLink.admin.common.convention.result.Results;
import com.study.shortLink.admin.dto.req.ShortLinkGroupSaveGroupReqDTO;
import com.study.shortLink.admin.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
}
