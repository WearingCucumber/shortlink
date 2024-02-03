package com.study.shortLink.admin.controller;

import com.study.shortLink.admin.common.convention.result.Result;
import com.study.shortLink.admin.remote.ShortLinkActualRemoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * URL标题控制层
 */
@RestController
@RequiredArgsConstructor
public class UrlTitleController {
    private final ShortLinkActualRemoteService shortLinkActualRemoteService;
    /**
     * 更具URL获取对应网站的标题
     * @param url
     * @return
     */
    @GetMapping("/api/short-link/admin/v1/title")
    public Result<String> getTitleByUrl(@RequestParam("url") String url) throws IOException {
        return shortLinkActualRemoteService.getTitleByUrl(url);
    }
}
