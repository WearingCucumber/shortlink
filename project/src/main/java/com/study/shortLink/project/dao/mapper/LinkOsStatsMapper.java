package com.study.shortLink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.study.shortLink.project.dao.entity.LinkOsStatsDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

public interface LinkOsStatsMapper extends BaseMapper<LinkOsStatsDO> {

    /**
     * 短链接监控操作系统数据
     * @param linkLocaleStatsDO
     */
    @Insert("INSERT INTO t_link_os_stats ( full_short_url, gid, date, cnt, os, create_time, update_time, del_flag )" +
            "VALUES( #{linkLocaleStats.fullShortUrl},  #{linkLocaleStats.gid},#{linkLocaleStats.date},#{linkLocaleStats.cnt}, #{linkLocaleStats.os}, NOW(), NOW(),0 ) " +
            "ON DUPLICATE KEY UPDATE cnt = cnt + #{linkLocaleStats.cnt}")
    void shortLinkOsStats(@Param("linkLocaleStats") LinkOsStatsDO linkLocaleStatsDO);

}
