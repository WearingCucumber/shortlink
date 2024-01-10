package com.study.shortLink.admin.remote;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.study.shortLink.admin.common.convention.result.Result;

import java.util.HashMap;

/**
 * 短链接URL标题远程调用服务
 */
public interface UrlTitleRemoteService {
    /**
     * 根据URL获取网站标题
     * @param url
     * @return
     */
    default Result<String> getTitleByUrl(String url) {
        HashMap<String, Object> requestParam = new HashMap<>();
        requestParam.put("url",url);
        String resultStr = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/title", requestParam);
        return JSON.parseObject(resultStr,new TypeReference<>() {
        });
    }
}
