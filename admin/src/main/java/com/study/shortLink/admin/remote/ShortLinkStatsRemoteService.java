package com.study.shortLink.admin.remote;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.study.shortLink.admin.common.convention.result.Result;
import com.study.shortLink.admin.remote.dto.req.ShortLinkStatsAccessRecordReqDTO;
import com.study.shortLink.admin.remote.dto.req.ShortLinkStatsReqDTO;
import com.study.shortLink.admin.remote.dto.resp.ShortLinkStatsAccessRecordRespDTO;
import com.study.shortLink.admin.remote.dto.resp.ShortLinkStatsRespDTO;

import java.util.HashMap;
import java.util.Map;

/**
 * 短链接监控中台远程调用服务
 */
public interface ShortLinkStatsRemoteService {
    /**
     * 获取单个短链接监控数据
     *
     * @param requestParam 获取短链接监控数据入参
     * @return 短链接监控数据
     */
    default Result<ShortLinkStatsRespDTO> oneShortLinkStats(ShortLinkStatsReqDTO requestParam){
        HashMap<String, Object> requestMap = new HashMap<>();
        requestMap.put("fullShortUrl",requestParam.getFullShortUrl());
        requestMap.put("gid",requestParam.getGid());
        requestMap.put("startDate",requestParam.getStartDate());
        requestMap.put("endDate",requestParam.getEndDate());
        String resultBody = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/stats", requestMap);
        return JSON.parseObject(resultBody, new TypeReference<Result<ShortLinkStatsRespDTO>>() {
        });
    }

    /**
     * 访问单个短链接指定时间内访问记录
     * @param requestParam
     * @return
     */

    default Result<IPage<ShortLinkStatsAccessRecordRespDTO>> shortLinkStatsAccessRecord(ShortLinkStatsAccessRecordReqDTO requestParam){
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("fullShortUrl",requestParam.getFullShortUrl());
        requestMap.put("gid",requestParam.getGid());
        requestMap.put("startDate",requestParam.getStartDate());
        requestMap.put("endDate",requestParam.getEndDate());
        requestMap.put("size",requestParam.getSize());
        requestMap.put("current",requestParam.getCurrent());
        String resultBody = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/stats/access-record", requestMap);
        return JSON.parseObject(resultBody, new TypeReference<>() {
        });
    }
}
