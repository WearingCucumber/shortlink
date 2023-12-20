package com.study.shortLink.admin.controller;

import com.study.shortLink.admin.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

/**
 * 分组管理控制层
 */
@RestController
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;
}
