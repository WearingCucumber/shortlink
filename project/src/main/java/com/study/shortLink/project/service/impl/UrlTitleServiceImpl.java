package com.study.shortLink.project.service.impl;

import com.study.shortLink.project.common.convention.exception.ClientException;
import com.study.shortLink.project.service.UrlTitleService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

/**
 * URL标题接口实现类
 */
@Service
public class UrlTitleServiceImpl implements UrlTitleService {
    @Override
    public String getTitleByUrl(String url) {
        try {
            Document document = null;
            document = Jsoup.connect(url).get();
            Elements titleElement = document.select("title");
            return titleElement.text();
        } catch (Exception e) {
            throw new ClientException("无法获取到网站标题,请检查输入网址是否正确");
        }
    }
}
