package com.study.shortLink.project.service;

import java.io.IOException;

/**
 * URL标题接口层
 */
public interface UrlTitleService {
    /**
     * 根据URL获取网站标题
     * @param url
     * @return
     */
    String getTitleByUrl(String url) throws IOException;
}
