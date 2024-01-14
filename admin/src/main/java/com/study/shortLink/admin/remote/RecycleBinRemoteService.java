package com.study.shortLink.admin.remote;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.study.shortLink.admin.common.convention.result.Result;
import com.study.shortLink.admin.remote.dto.req.RecycleBinPageReqDTO;
import com.study.shortLink.admin.remote.dto.req.RecycleBinSaveReqDTO;
import com.study.shortLink.admin.remote.dto.resp.ShortLinkPageRespDTO;

import java.util.HashMap;
import java.util.Map;

public interface RecycleBinRemoteService {
    /**
     * 保存回收站
     * @param requestParam 请求参数
     */
    default void saveRecycleBin(RecycleBinSaveReqDTO requestParam){
        String jsonBody = JSON.toJSON(requestParam).toString();
        HttpUtil.post("http://127.0.0.1:8001/api/short-link/v1/recycle-bin/save",jsonBody);
    }

    /**
     * 分页查询
     * @param requestParam
     * @return
     */
    default Result<IPage<ShortLinkPageRespDTO>> recycleBinPage(RecycleBinPageReqDTO requestParam){
        Map<String,Object> requestMap = new HashMap<>();
        requestMap.put("gidList",requestParam.getGidList());
        requestMap.put("size",requestParam.getSize());
        requestMap.put("current",requestParam.getCurrent());
        String resultPageStr = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/recycle-bin/page", requestMap);
        return JSON.parseObject(resultPageStr, new TypeReference<>() {});
    }
}
