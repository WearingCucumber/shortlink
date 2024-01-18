package com.study.shortLink.project.toolkit;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.study.shortLink.project.dao.entity.LinkLocaleStatsDO;
import jakarta.servlet.http.HttpServletRequest;

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


    /**
     * 获取用户真实IP
     * @param request
     * @return
     */
    public static String getClientIp(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");

        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }

        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }

        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
        }

        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_X_FORWARDED");
        }

        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_X_CLUSTER_CLIENT_IP");
        }

        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_CLIENT_IP");
        }

        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_FORWARDED_FOR");
        }

        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_FORWARDED");
        }

        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_VIA");
        }

        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("REMOTE_ADDR");
        }

        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }

        return ipAddress;
    }

    /**
     * 通过高德地图API获取ip对应位置信息
     * @param ip
     * @param APIKEY
     * @return
     */
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

    /**
     * 获取用户操作系统数据
     * @param request
     * @return
     */
    public static String getUserOS(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");

        if (userAgent == null) {
            return "unknown";
        }

        // 在实际应用中，你可能需要使用更复杂的逻辑来解析用户代理字符串
        if (userAgent.toLowerCase().contains("windows")) {
            return "windows";
        } else if (userAgent.toLowerCase().contains("mac")) {
            return "mac os";
        } else if (userAgent.toLowerCase().contains("linux")) {
            return "linux";
        } else if (userAgent.toLowerCase().contains("android")) {
            return "android";
        } else if (userAgent.toLowerCase().contains("iphone") || userAgent.toLowerCase().contains("ipad")) {
            return "ios";
        } else {
            return "unknown";
        }
    }
    /**
     * 获取用户访问浏览器
     *
     * @param request 请求
     * @return 访问浏览器
     */
    public static String getBrowser(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent.toLowerCase().contains("edg")) {
            return "Microsoft Edge";
        } else if (userAgent.toLowerCase().contains("chrome")) {
            return "Google Chrome";
        } else if (userAgent.toLowerCase().contains("firefox")) {
            return "Mozilla Firefox";
        } else if (userAgent.toLowerCase().contains("safari")) {
            return "Apple Safari";
        } else if (userAgent.toLowerCase().contains("opera")) {
            return "Opera";
        } else if (userAgent.toLowerCase().contains("msie") || userAgent.toLowerCase().contains("trident")) {
            return "Internet Explorer";
        } else {
            return "Unknown";
        }
    }


    /**
     * 获取用户访问设备
     *
     * @param request 请求
     * @return 访问设备
     */
    public static String getDevice(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent.toLowerCase().contains("mobile")) {
            return "Mobile";
        }
        return "PC";
    }


    /**
     * 获取用户访问网络
     *
     * @param request 请求
     * @return 访问设备
     */
    public static String getNetwork(HttpServletRequest request) {
        String actualIp = getClientIp(request);
        // 这里简单判断IP地址范围，您可能需要更复杂的逻辑
        // 例如，通过调用IP地址库或调用第三方服务来判断网络类型
        return actualIp.startsWith("192.168.") || actualIp.startsWith("10.") ? "WIFI" : "Mobile";
    }


}
