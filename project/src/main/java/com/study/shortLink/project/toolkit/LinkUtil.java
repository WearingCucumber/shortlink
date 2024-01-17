package com.study.shortLink.project.toolkit;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.study.shortLink.project.dao.entity.LinkLocaleStatsDO;

import java.util.Date;
import java.util.HashMap;
import java.util.Optional;

import static com.study.shortLink.project.common.constant.ShortLinkConstant.DEFAULT_CACHE_VALID_TIME;

/**
 * 短链接工具类
 */
public class LinkUtil {
    /**
     * 获取短链接缓存有效期时间
     * @param validDate 有效期时间
     * @return 有效期时间戳
     */
    public static long getLinkCacheValidDate(Date validDate){
        return Optional.ofNullable(validDate).map(each-> DateUtil.between(new Date(),each,DateUnit.MS)).orElse(DEFAULT_CACHE_VALID_TIME);
    }
    public static LinkLocaleStatsDO getAddrByIP(String ip , String APIKEY){
        HashMap<String, Object> requestBody = new HashMap<>();
        requestBody.put("key",APIKEY);
        requestBody.put("ip",ip);
        String jsonBody = HttpUtil.get("https://restapi.amap.com/v3/ip", requestBody);
        JSONObject jsonObject = JSON.parseObject(jsonBody);
        if (StringUtils.isNotBlank(jsonBody) &&  StringUtils.equals(jsonObject.getString("infocode"),"1000"))
            return JSON.parseObject(jsonBody, LinkLocaleStatsDO.class);
        return new LinkLocaleStatsDO();
    }

    public static void main(String[] args) {
        System.out.println(getAddrByIP("183.210.251.215", "7d45420859975512ad10090967c84cfd"));
    }
}
