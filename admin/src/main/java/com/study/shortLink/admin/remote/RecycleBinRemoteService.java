package com.study.shortLink.admin.remote;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.study.shortLink.admin.remote.dto.req.RecycleBinSaveReqDTO;

public interface RecycleBinRemoteService {
    /**
     * 保存回收站
     * @param requestParam 请求参数
     */
    default void saveRecycleBin(RecycleBinSaveReqDTO requestParam){
        String jsonBody = JSON.toJSON(requestParam).toString();
        HttpUtil.post("http://127.0.0.1:8001/api/short-link/v1/recycle-bin/save",jsonBody);
    }
}
