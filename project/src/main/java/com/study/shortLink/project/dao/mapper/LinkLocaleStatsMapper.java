package com.study.shortLink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.study.shortLink.project.dao.entity.LinkLocaleStatsDO;
import com.study.shortLink.project.dto.req.ShortLinkStatsReqDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 地区统计访问持久层
 */
public interface LinkLocaleStatsMapper extends BaseMapper<LinkLocaleStatsDO> {
    /**
     * 记录地区访问监控数据
     * @param linkLocaleStatsDO
     */
    @Insert("INSERT INTO t_link_locale_stats ( full_short_url, gid, date, cnt, province, city, adcode, country, create_time, update_time, del_flag )" +
            "VALUES( #{LocaleStats.fullShortUrl},  #{LocaleStats.gid},#{LocaleStats.date},#{LocaleStats.cnt}, #{LocaleStats.province}, #{LocaleStats.city}, #{LocaleStats.adcode},#{LocaleStats.country}, NOW(), NOW(),0 ) " +
            "ON DUPLICATE KEY UPDATE cnt = cnt + #{LocaleStats.cnt}")
    void shortLinkLocaleState(@Param("LocaleStats") LinkLocaleStatsDO linkLocaleStatsDO);

    /**
     * 根据短链接获取指定日期内基础监控数据
     */
    @Select("SELECT " +
            "    province, " +
            "    SUM(cnt) AS cnt " +
            "FROM " +
            "    t_link_locale_stats " +
            "WHERE " +
            "    full_short_url = #{param.fullShortUrl} " +
            "    AND gid = #{param.gid} " +
            "    AND date BETWEEN #{param.startDate} and #{param.endDate} " +
            "GROUP BY " +
            "    full_short_url, gid, province;")
    List<LinkLocaleStatsDO> listLocaleByShortLink(@Param("param") ShortLinkStatsReqDTO requestParam);
}
