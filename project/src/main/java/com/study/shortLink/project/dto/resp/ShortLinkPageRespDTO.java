package com.study.shortLink.project.dto.resp;

import lombok.Data;

import java.util.Date;

/**
 * 短链接分页返回参数
 */
@Data
public class ShortLinkPageRespDTO {
    /**
     * 域名
     */
    private String domain;

    /**
     * 短链接路径
     */
    private String shortUri;

    /**
     * 完整短链接
     */
    private String fullShortUrl;

    /**
     * 原始链接
     */
    private String originUrl;

    /**
     * 点击量
     */
    private Integer clickNum;

    /**
     * 分组标识
     */
    private String gid;


    /**
     * 创建类型 0:接口创建 1:平台创建
     */
    private int createType;

    /**
     * 有效期类型 0:永久有效 1:自定义
     */
    private int validDateType;

    /**
     * 有效期
     */
    private Date validDate;

    /**
     * 描述
     */
    private String description;

}
