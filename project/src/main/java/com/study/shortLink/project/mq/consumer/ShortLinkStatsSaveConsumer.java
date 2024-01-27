package com.study.shortLink.project.mq.consumer;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.Week;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.study.shortLink.project.dao.entity.*;
import com.study.shortLink.project.dao.mapper.*;
import com.study.shortLink.project.mq.domain.MessageWrapper;
import com.study.shortLink.project.mq.event.ShortLinkStatsEvent;
import com.study.shortLink.project.toolkit.LinkUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static com.study.shortLink.project.common.constant.ShortLinkStatsMQConstant.*;

@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(
        topic = LINK_STATS_TOPIC_KEY,
        consumerGroup = LINK_STATS_SAVE_CG_KEY,
        selectorExpression = LINK_STATS_SAVE_TAG_KEY

)
public class ShortLinkStatsSaveConsumer implements RocketMQListener<MessageWrapper<ShortLinkStatsEvent>> {
    @Value("${short-link.stats.locale.amap-key}")
    private String statsLocaleAmapKey;
    private final ShortLinkGotoMapper shortLinkGotoMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final LinkAccessStatsMapper linkAccessStatsMapper;
    private final LinkLocaleStatsMapper linkLocaleStatsMapper;
    private final LinkOsStatsMapper linkOsStatsMapper;
    private final LinkBrowserStatsMapper linkBrowserStatsMapper;
    private final LinkDeviceStatsMapper linkDeviceStatsMapper;
    private final LinkNetworkStatsMapper linkNetworkStatsMapper;
    private final LinkAccessLogsMapper linkAccessLogsMapper;
    @Override
    public void onMessage(MessageWrapper<ShortLinkStatsEvent> message) {
        ShortLinkStatsEvent shortLinkStatsEvent = message.getMessage();
        String fullShortUrl = shortLinkStatsEvent.getFullShortUrl();
        String shortUrl = shortLinkStatsEvent.getShortUrl();
        AtomicBoolean uvFirstFlag = shortLinkStatsEvent.getUvFirstFlag();
        String gid = shortLinkStatsEvent.getGid();
        AtomicReference<String> uv  = shortLinkStatsEvent.getUv();
        Map<String, String> request = shortLinkStatsEvent.getRequest();
        //用于统计uip 通过redis set 判断集合中是否有这个ip 来判断uip是否要增加
        String remoteAddr = request.get("ClientIp");
        Long uipAdded = stringRedisTemplate.opsForSet().add("short-link:stats:uip:" + shortUrl, remoteAddr);
        boolean uipFirstTag = uipAdded != null && uipAdded > 0;
        if (StringUtils.isBlank(gid)) {
            LambdaQueryWrapper<ShortLinkGotoDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkGotoDO.class)
                    .eq(ShortLinkGotoDO::getFullShortUrl, fullShortUrl);
            ShortLinkGotoDO shortLinkGotoDO = shortLinkGotoMapper.selectOne(queryWrapper);
            gid = shortLinkGotoDO.getGid();
        }
        Date date = new Date();
        Week week = DateUtil.dayOfWeekEnum(date);
        int weekValue = week.getIso8601Value();
        int hour = DateUtil.hour(date, true);
        LinkAccessStatsDO linkAccessStatsDO = LinkAccessStatsDO.builder()
                .pv(1)   // 只要短链接被点击 每次都会加一
                .uv(uvFirstFlag.get() ? 1 : 0)
                .uip(uipFirstTag ? 1 : 0)
                .hour(hour)
                .weekday(weekValue)
                .date(date)
                .gid(gid)
                .fullShortUrl(fullShortUrl)
                .build();
        //基本监控数据插入
        linkAccessStatsMapper.shortLinkStats(linkAccessStatsDO);
        //通过高德API获取 ip对应的 地区
        LinkLocaleStatsDO amapLocaleStatsDO = LinkUtil.getAddrByIP(remoteAddr, statsLocaleAmapKey);
        String actualProvince ;
        String actualCity;
        LinkLocaleStatsDO linkLocaleStatsDO = LinkLocaleStatsDO.builder()
                .date(date)
                .city(actualCity = StringUtils.isBlank(amapLocaleStatsDO.getCity()) ? "未知" : amapLocaleStatsDO.getCity())
                .province(actualProvince = StringUtils.isBlank(amapLocaleStatsDO.getProvince()) ? "未知" : amapLocaleStatsDO.getProvince())
                .adcode(StringUtils.isBlank(amapLocaleStatsDO.getAdcode()) ? "未知" : amapLocaleStatsDO.getAdcode())
                .cnt(1)
                .fullShortUrl(fullShortUrl)
                .gid(gid)
                .country("中国")
                .build();
        //地区监控数据插入
        linkLocaleStatsMapper.shortLinkLocaleState(linkLocaleStatsDO);
        String os = request.get("UserOS");
        LinkOsStatsDO linkOsStatsDO = LinkOsStatsDO.builder()
                .os(os)
                .gid(gid)
                .cnt(1)
                .date(date)
                .fullShortUrl(fullShortUrl)
                .build();
        //操作系统监控数据插入
        linkOsStatsMapper.shortLinkOsStats(linkOsStatsDO);
        String browser = request.get("Browser");
        LinkBrowserStatsDO linkBrowserStatsDO = LinkBrowserStatsDO.builder()
                .browser(browser)
                .cnt(1)
                .gid(gid)
                .fullShortUrl(fullShortUrl)
                .date(date)
                .build();
        //浏览器标识数据插入
        linkBrowserStatsMapper.shortLinkBrowserState(linkBrowserStatsDO);
        String device = request.get("Device");
        LinkDeviceStatsDO linkDeviceStatsDO = LinkDeviceStatsDO.builder()
                .device(device)
                .cnt(1)
                .gid(gid)
                .fullShortUrl(fullShortUrl)
                .date(new Date())
                .build();
        //设备信息监控数据插入
        linkDeviceStatsMapper.shortLinkDeviceState(linkDeviceStatsDO);
        String network = request.get("Network");
        LinkNetworkStatsDO linkNetworkStatsDO = LinkNetworkStatsDO.builder()
                .network(network)
                .cnt(1)
                .gid(gid)
                .fullShortUrl(fullShortUrl)
                .date(new Date())
                .build();
        //网络信息监控数据插入
        linkNetworkStatsMapper.shortLinkNetworkState(linkNetworkStatsDO);

        LinkAccessLogsDO linkAccessLogsDO = LinkAccessLogsDO.builder()
                .ip(remoteAddr)
                .os(os)
                .browser(browser)
                .device(device)
                .network(network)
                .fullShortUrl(fullShortUrl)
                .gid(gid)
                .user(uv.get())
                .locale(StrUtil.join("-","中国",actualProvince,actualCity))
                .build();
        //短链接访问记录数据插入
        linkAccessLogsMapper.insert(linkAccessLogsDO);


    }
}
