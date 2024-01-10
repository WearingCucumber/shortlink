package com.study.shortLink.project.controller;

import com.study.shortLink.project.common.convention.result.Result;
import com.study.shortLink.project.common.convention.result.Results;
import com.study.shortLink.project.service.UrlTitleService;
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
    private final UrlTitleService urlTitleService;
    /**
     * 更具URL获取对应网站的标题
     * @param url
     * @return
     */
    @GetMapping("/api/short-link/v1/title")
    public Result<String> getTitleByUrl(@RequestParam("url") String url) throws IOException {
        String title = urlTitleService.getTitleByUrl(url);
        return Results.success(title);
    }
}
