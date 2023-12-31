package com.study.shortLink.project.dao.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.study.shortLink.project.common.database.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 短连接实体
 */
@TableName("t_link")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShortLinkDO extends BaseDO {
    @TableId(type = IdType.AUTO)
    /**
     * id
     */
    private Long id;

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
     * 启用标识 0:启用 1:未启用
     */
    private Integer enableStatus;

    /**
     * 创建类型 0:接口创建 1:平台创建
     */
    private Integer createType;

    /**
     * 有效期类型 0:永久有效 1:自定义
     */
    private Integer validDateType;

    /**
     * 有效期
     */
    private Date validDate;

    /**
     * 描述
     */
    private String description;
    /**
     * 图片
     */
    private String favicon;

}
