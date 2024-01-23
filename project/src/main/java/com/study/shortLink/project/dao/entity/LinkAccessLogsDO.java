package com.study.shortLink.project.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.study.shortLink.project.common.database.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 短链接访问记录数据实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_link_access_logs")
public class LinkAccessLogsDO  extends BaseDO {
    /**
     * id
     */
    private Long id;

    /**
     * 完整短链接
     */
    private String fullShortUrl;

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 用户标识
     */
    private String user;

    /**
     * 浏览器
     */
    private String browser;

    /**
     * 操作系统
     */
    private String os;

    /**
     * ip地址
     */
    private String ip;

    /**
     * 访问设备
     */
    private String device;

    /**
     * 访问网络
     */
    private String network;

    /**
     * 地区
     */
    private String locale;


}
