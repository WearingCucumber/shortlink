package com.study.shortLink.project.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.study.shortLink.project.common.database.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 *  地区统计访问实体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_link_os_stats")
public class LinkOsStatsDO extends BaseDO {
    /**
     * ID
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
     * 日期
     */
    private Date date;

    /**
     * 访问量
     */
    private Integer cnt;

    /**
     * 操作系统
     */
    private String os;



}
