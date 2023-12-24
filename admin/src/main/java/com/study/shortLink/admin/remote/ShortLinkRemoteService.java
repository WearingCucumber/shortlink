package com.study.shortLink.admin.remote;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.study.shortLink.admin.common.convention.result.Result;
import com.study.shortLink.admin.remote.dto.req.ShortLinkCreateReqDTO;
import com.study.shortLink.admin.remote.dto.req.ShortLinkPageReqDTO;
import com.study.shortLink.admin.remote.dto.resp.ShortLinkCreateRespDTO;
import com.study.shortLink.admin.remote.dto.resp.ShortLinkPageRespDTO;

import java.util.HashMap;
import java.util.Map;

/**
 * 短链接中台远程调用服务
 */
public interface ShortLinkRemoteService {
    /**
     * 短链接分页查询
     * @param requestParam 分页查询参数
     * @return
     */
    default Result<IPage<ShortLinkPageRespDTO>> page(ShortLinkPageReqDTO requestParam){
        Map<String,Object> requestMap = new HashMap<>();
        requestMap.put("gid",requestParam.getGid());
        requestMap.put("size",requestParam.getSize());
        requestMap.put("current",requestParam.getCurrent());
        String resultPageStr = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/page", requestMap);
        return JSON.parseObject(resultPageStr, new TypeReference<>() {
        });

    }

    /**
     * 创建短链接
     * @param requestParam 创建参数
     * @return
     */
    default   Result<ShortLinkCreateRespDTO> createShortLink( ShortLinkCreateReqDTO requestParam){
        String jsonBody = JSON.toJSON(requestParam).toString();
        String result = HttpUtil.post("http://127.0.0.1:8001/api/short-link/v1/create", jsonBody);
        return JSON.parseObject(result, new TypeReference<Result<ShortLinkCreateRespDTO>>() {
        });
    }
}
