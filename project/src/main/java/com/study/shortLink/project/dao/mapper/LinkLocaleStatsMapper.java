package com.study.shortLink.project.dao.mapper;

import com.study.shortLink.project.dao.entity.LinkLocaleStatsDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

/**
 * 地区统计访问持久层
 */
public interface LinkLocaleStatsMapper {
    /**
     * 记录地区访问监控数据
     * @param linkLocaleStatsDO
     */
    @Insert("INSERT INTO t_link_locale_stats ( full_short_url, gid, date, cnt, province, city, adcode, country, create_time, update_time, del_flag )" +
            "VALUES( #{LocaleStats.fullShortUrl},  #{LocaleStats.gid},#{LocaleStats.date},#{LocaleStats.cnt}, #{LocaleStats.province}, #{LocaleStats.city}, #{LocaleStats.adcode},#{LocaleStats.country}, NOW(), NOW(),0 ) " +
            "ON DUPLICATE KEY UPDATE cnt = cnt + #{LocaleStats.cnt}")
    void shortLinkLocaleState(@Param("LocaleStats") LinkLocaleStatsDO linkLocaleStatsDO);
}
