package com.study.shortLink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.study.shortLink.project.dao.entity.LinkOsStatsDO;
import com.study.shortLink.project.dto.req.ShortLinkStatsReqDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.HashMap;
import java.util.List;

public interface LinkOsStatsMapper extends BaseMapper<LinkOsStatsDO> {

    /**
     * 短链接监控操作系统数据
     * @param linkLocaleStatsDO
     */
    @Insert("INSERT INTO t_link_os_stats ( full_short_url, gid, date, cnt, os, create_time, update_time, del_flag )" +
            "VALUES( #{linkLocaleStats.fullShortUrl},  #{linkLocaleStats.gid},#{linkLocaleStats.date},#{linkLocaleStats.cnt}, #{linkLocaleStats.os}, NOW(), NOW(),0 ) " +
            "ON DUPLICATE KEY UPDATE cnt = cnt + #{linkLocaleStats.cnt}")
    void shortLinkOsStats(@Param("linkLocaleStats") LinkOsStatsDO linkLocaleStatsDO);


    /**
     * 根据短链接获取指定日期内操作系统监控数据
     */
    @Select("SELECT " +
            "    os, " +
            "    SUM(cnt) AS count " +
            "FROM " +
            "    t_link_os_stats " +
            "WHERE " +
            "    full_short_url = #{param.fullShortUrl} " +
            "    AND gid = #{param.gid} " +
            "    AND date BETWEEN #{param.startDate} and #{param.endDate} " +
            "GROUP BY " +
            "    full_short_url, gid, date, os;")
    List<HashMap<String, Object>> listOsStatsByShortLink(@Param("param") ShortLinkStatsReqDTO requestParam);

}
