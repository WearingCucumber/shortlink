package com.study.shortLink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.Week;
import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ArrayUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.study.shortLink.project.common.convention.exception.ClientException;
import com.study.shortLink.project.common.convention.exception.ServiceException;
import com.study.shortLink.project.dao.entity.LinkAccessStatsDO;
import com.study.shortLink.project.dao.entity.LinkLocaleStatsDO;
import com.study.shortLink.project.dao.entity.ShortLinkDO;
import com.study.shortLink.project.dao.entity.ShortLinkGotoDO;
import com.study.shortLink.project.dao.mapper.LinkAccessStatsMapper;
import com.study.shortLink.project.dao.mapper.LinkLocaleStatsMapper;
import com.study.shortLink.project.dao.mapper.ShortLinkGotoMapper;
import com.study.shortLink.project.dao.mapper.ShortLinkMapper;
import com.study.shortLink.project.dto.req.ShortLinkCreateReqDTO;
import com.study.shortLink.project.dto.req.ShortLinkPageReqDTO;
import com.study.shortLink.project.dto.req.ShortLinkUpdateReqDTO;
import com.study.shortLink.project.dto.resp.ShortLinkCreateRespDTO;
import com.study.shortLink.project.dto.resp.ShortLinkGroupCountQueryRespDTO;
import com.study.shortLink.project.dto.resp.ShortLinkPageRespDTO;
import com.study.shortLink.project.service.ShortLinkService;
import com.study.shortLink.project.toolkit.HashUtil;
import com.study.shortLink.project.toolkit.LinkUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.study.shortLink.project.common.constant.RedisKeyConstant.*;
import static com.study.shortLink.project.common.enums.ValiDateTypeEnum.PERMANENT;

/**
 * 短链接接口实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ShortLinkServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements ShortLinkService {
    @Value("${short-link.stats.locale.amap-key}")
    private String statsLocaleAmapKey;
    private final RBloomFilter<String> shortUriCreateCachePenetrationBloomFilter;
    private final ShortLinkGotoMapper shortLinkGotoMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final RedissonClient redissonClient;
    private final LinkAccessStatsMapper linkAccessStatsMapper;
    private final LinkLocaleStatsMapper linkLocaleStatsMapper;

    @Override
    public void redirectUrl(String shortUrl, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String serverName = request.getServerName();
        String scheme = request.getScheme();
        String fullShortUrl = scheme + "://" + serverName + "/" + shortUrl;
        /**
         * 防止缓存穿透
         */
        //这里先判断布隆过滤器中是否存在该链接
        if (!shortUriCreateCachePenetrationBloomFilter.contains(fullShortUrl)) {
            response.sendRedirect("/page/notfound");
            return;
        }
        //这里防止布隆过滤器误判导致多个同一个不存在的短链接请求打进来 因为同一个请求既然布隆过滤器误判那么后面同样请求都会误判  这里加一个 isnull可以来防止这种情况发生
        String gotoIsNullShortLink = stringRedisTemplate.opsForValue().get(String.format(GOTO_IS_NULL_SHORT_LINK_KEY, shortUrl));
        if (StringUtils.isNotBlank(gotoIsNullShortLink)) {
            response.sendRedirect("/page/notfound");
            return;
        }
        /**
         * 防止缓存击穿
         */
        String originalLink = stringRedisTemplate.opsForValue().get(String.format(GOTO_SHORT_LINK_KEY, shortUrl));
        ShortLinkDO shortLinkDO;
        if (StringUtils.isNotBlank(originalLink)) {
            shortLinkStats(shortUrl, null, request, response);
            response.sendRedirect(originalLink);
            return;
        }
        RLock lock = redissonClient.getLock(String.format(LOCK_GOTO_SHORT_LINK_KEY, shortUrl));
        lock.lock();
        try {
            originalLink = stringRedisTemplate.opsForValue().get(String.format(GOTO_SHORT_LINK_KEY, shortUrl));
            if (StringUtils.isNotBlank(originalLink)) {
                shortLinkStats(shortUrl, null, request, response);
                response.sendRedirect(originalLink);
                return;
            }
            LambdaQueryWrapper<ShortLinkGotoDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkGotoDO.class)
                    .eq(ShortLinkGotoDO::getFullShortUrl, fullShortUrl);
            ShortLinkGotoDO shortLinkGotoDO = shortLinkGotoMapper.selectOne(queryWrapper);
            if (shortLinkGotoDO == null) {
                //这里进行风控
                stringRedisTemplate.opsForValue().set(String.format(GOTO_IS_NULL_SHORT_LINK_KEY, shortUrl), "-", 30, TimeUnit.MINUTES);
                response.sendRedirect("/page/notfound");
                return;
            }
            LambdaQueryWrapper<ShortLinkDO> shortLinkDOQueryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                    .eq(ShortLinkDO::getGid, shortLinkGotoDO.getGid())
                    .eq(ShortLinkDO::getFullShortUrl, shortLinkGotoDO.getFullShortUrl())
                    .eq(ShortLinkDO::getEnableStatus, 0)
                    .eq(ShortLinkDO::getDelFlag, 0);
            shortLinkDO = baseMapper.selectOne(shortLinkDOQueryWrapper);
            //判断数据库中是否有对应数据
            if (shortLinkDO != null) {
                if (shortLinkDO.getValidDate() != null && shortLinkDO.getValidDate().before(new Date())) {
                    stringRedisTemplate.opsForValue().set(String.format(GOTO_IS_NULL_SHORT_LINK_KEY, shortUrl), "-", 30, TimeUnit.MINUTES);
                    response.sendRedirect("/page/notfound");
                    return;
                }
                stringRedisTemplate.opsForValue().set(
                        String.format(GOTO_SHORT_LINK_KEY, shortUrl),
                        shortLinkDO.getOriginUrl(),
                        LinkUtil.getLinkCacheValidDate(shortLinkDO.getValidDate()),
                        TimeUnit.MILLISECONDS);
                shortLinkStats(shortUrl, shortLinkDO.getGid(), request, response);
                response.sendRedirect(shortLinkDO.getOriginUrl());
            } else {
                stringRedisTemplate.opsForValue().set(String.format(GOTO_IS_NULL_SHORT_LINK_KEY, shortUrl), "-", 30, TimeUnit.MINUTES);
                response.sendRedirect("/page/notfound");
            }
        } finally {
            lock.unlock();
        }

    }

    private void shortLinkStats(String shortUrl, String gid, HttpServletRequest request, HttpServletResponse response) {
        AtomicBoolean uvFirstFlag = new AtomicBoolean();
        String serverName = request.getServerName();
        String scheme = request.getScheme();
        String fullShortUrl = scheme + "://" + serverName + "/" + shortUrl;
        try {

            Runnable addResponseCookieTask = () -> {
                String uv = UUID.fastUUID().toString();
                Cookie uvCookie = new Cookie("uv", uv);
                uvCookie.setMaxAge(60 * 60 * 24 * 30);
                uvCookie.setPath("/" + shortUrl);
                response.addCookie(uvCookie);
                uvFirstFlag.set(Boolean.TRUE);
                stringRedisTemplate.opsForSet().add("short-link:stats:uv:" + shortUrl, uv);
            };
            Cookie[] cookies = request.getCookies();
            //用于统计UV 判断cookie中是否存在uv变量 存在则 获取加入redis中  不存在则创建cookie 加入redis中
            if (ArrayUtils.isNotEmpty(cookies)) {
                Arrays.stream(cookies)
                        .filter(each -> Objects.equals(each.getName(), "uv"))
                        .findFirst()
                        .map(Cookie::getValue)
                        .ifPresentOrElse(each -> {
                            Long uvAdded = stringRedisTemplate.opsForSet().add("short-link:stats:uv:" + shortUrl, each);
                            uvFirstFlag.set(uvAdded != null && uvAdded > 0);
                        }, addResponseCookieTask);
            } else {
                addResponseCookieTask.run();
            }
            //用于统计uip 通过redis set 判断集合中是否有这个ip 来判断uip是否要增加
            String remoteAddr = request.getRemoteAddr();
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
            linkAccessStatsMapper.shortLinkStats(linkAccessStatsDO);
            //通过高德API获取 ip对应的 地区
            LinkLocaleStatsDO amapLocaleStatsDO = LinkUtil.getAddrByIP(remoteAddr, statsLocaleAmapKey);
            LinkLocaleStatsDO linkLocaleStatsDO = LinkLocaleStatsDO.builder()
                    .date(date)
                    .city(StringUtils.isBlank(amapLocaleStatsDO.getCity()) ? "未知" : amapLocaleStatsDO.getCity())
                    .province(StringUtils.isBlank(amapLocaleStatsDO.getProvince()) ? "未知" : amapLocaleStatsDO.getProvince())
                    .adcode(StringUtils.isBlank(amapLocaleStatsDO.getAdcode()) ? "未知" : amapLocaleStatsDO.getAdcode())
                    .cnt(1)
                    .fullShortUrl(fullShortUrl)
                    .gid(gid)
                    .country("中国")
                    .build();
            linkLocaleStatsMapper.shortLinkLocaleState(linkLocaleStatsDO);


        } catch (Throwable ex) {
            throw ex;
        }
    }


    @Override
    @Transactional
    public ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam) {
        String shortLinkSuffix = generateSuffix(requestParam);
        String fullShortUrl = requestParam.getDomain() + "/" + shortLinkSuffix;
        ShortLinkDO shortLinkDO = BeanUtil.toBean(requestParam, ShortLinkDO.class);
        shortLinkDO.setFullShortUrl(fullShortUrl);
        shortLinkDO.setShortUri(shortLinkSuffix);
        shortLinkDO.setFavicon(getIcon(requestParam.getOriginUrl()));
        shortLinkDO.setEnableStatus(0);
        try {
            baseMapper.insert(shortLinkDO);
            ShortLinkGotoDO shortLinkGotoDO = ShortLinkGotoDO.builder()
                    .fullShortUrl(fullShortUrl)
                    .gid(requestParam.getGid())
                    .build();
            shortLinkGotoMapper.insert(shortLinkGotoDO);
        } catch (DuplicateKeyException ex) {
/*            LambdaQueryWrapper<ShortLinkDO> wrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                    .eq(ShortLinkDO::getFullShortUrl, fullShortUrl);
            Long count = baseMapper.selectCount(wrapper);
            if (count != 0) {
            }
          */
            log.warn("短链接:{} 重复入库", fullShortUrl);
            shortUriCreateCachePenetrationBloomFilter.add(fullShortUrl);
            throw new ServiceException("短链接生成重复");
        }
        shortUriCreateCachePenetrationBloomFilter.add(fullShortUrl);
        /**
         * 缓存预热
         */
        stringRedisTemplate.opsForValue().set(
                String.format(GOTO_SHORT_LINK_KEY, shortLinkSuffix), requestParam.getOriginUrl(),
                LinkUtil.getLinkCacheValidDate(requestParam.getValidDate()),
                TimeUnit.MILLISECONDS
        );
        return ShortLinkCreateRespDTO.builder()
                .fullShortUrl(shortLinkDO.getFullShortUrl())
                .originUrl(requestParam.getOriginUrl())
                .gid(requestParam.getGid())
                .build();
    }

    @Override
    public IPage<ShortLinkPageRespDTO> shortLinkPage(ShortLinkPageReqDTO requestParam) {
        LambdaQueryWrapper<ShortLinkDO> wrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, requestParam.getGid())
                .eq(ShortLinkDO::getDelFlag, 0)
                .eq(ShortLinkDO::getEnableStatus, 0)
                .orderByDesc(ShortLinkDO::getCreateTime);
        IPage<ShortLinkDO> page = baseMapper.selectPage(requestParam, wrapper);
        return page.convert(each -> BeanUtil.toBean(each, ShortLinkPageRespDTO.class));
    }

    @Override
    public List<ShortLinkGroupCountQueryRespDTO> listGroupShortLinkCount(List<String> requestParam) {
        QueryWrapper<ShortLinkDO> queryWrapper = Wrappers.query(new ShortLinkDO())
                .select("gid as gid", "count(*) as shortLinkCount")
                .in("gid", requestParam)
                .eq("enable_status", 0)
                .groupBy("gid");
        List<Map<String, Object>> list = baseMapper.selectMaps(queryWrapper);
        return BeanUtil.copyToList(list, ShortLinkGroupCountQueryRespDTO.class);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateShortLink(ShortLinkUpdateReqDTO requestParam) {
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, requestParam.getOriginGid())
                .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                .eq(ShortLinkDO::getDelFlag, 0)
                .eq(ShortLinkDO::getEnableStatus, 0);
        //先从数据库查询要修改的记录
        ShortLinkDO hasShortLink = baseMapper.selectOne(queryWrapper);
        if (hasShortLink == null) {
            throw new ClientException("该记录不存在");
        } else {
            //构建一个新的DO来保存原来的数据用于做数据迁移
            ShortLinkDO shortLinkDo = ShortLinkDO.builder()
                    .domain(hasShortLink.getDomain())
                    .fullShortUrl(hasShortLink.getFullShortUrl())
                    .shortUri(hasShortLink.getShortUri())
                    .clickNum(hasShortLink.getClickNum())
                    .favicon(hasShortLink.getFavicon())
                    .createType(hasShortLink.getCreateType())
                    .validDateType(requestParam.getCreateType())
                    .validDate(requestParam.getValidDate())
                    .originUrl(requestParam.getOriginUrl())
                    .gid(requestParam.getGid())
                    .description(requestParam.getDescription()).build();
            ShortLinkGotoDO shortLinkGotoDO = ShortLinkGotoDO.builder()
                    .gid(requestParam.getGid())
                    .build();
            //如果这里的传过来的gid 和原来的 gid 不相等说明需要做数据迁移 相等则直接按照条件更新
            if (Objects.equals(requestParam.getOriginGid(), requestParam.getGid())) {
                LambdaUpdateWrapper<ShortLinkDO> updateWrapper = Wrappers.lambdaUpdate(ShortLinkDO.class)
                        .eq(ShortLinkDO::getGid, requestParam.getGid())
                        .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                        .eq(ShortLinkDO::getDelFlag, 0)
                        .eq(ShortLinkDO::getEnableStatus, 0)
                        //这里用来判断有效期类型是否是永久有效 如果是的话则直接设置有效期时间为null
                        .set(Objects.equals(requestParam.getValidDateType(), PERMANENT.getType()), ShortLinkDO::getValidDate, null);
                baseMapper.update(shortLinkDo, updateWrapper);
            } else {
                LambdaUpdateWrapper<ShortLinkDO> updateWrapper = Wrappers.lambdaUpdate(ShortLinkDO.class)
                        .eq(ShortLinkDO::getGid, requestParam.getOriginGid())
                        .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl());
                ShortLinkDO shortLinkDOToDel = new ShortLinkDO();
                shortLinkDOToDel.setDelFlag(1);
                //这里做软删除操作将原来的数据删除
                baseMapper.update(shortLinkDOToDel, updateWrapper);
                //数据迁移操作
                baseMapper.insert(shortLinkDo);
            }
            LambdaUpdateWrapper<ShortLinkGotoDO> shortLinkGotoUpdateWrapper = Wrappers.lambdaUpdate(ShortLinkGotoDO.class)
                    .eq(ShortLinkGotoDO::getFullShortUrl, requestParam.getFullShortUrl())
                    .eq(ShortLinkGotoDO::getGid, requestParam.getGid());
            shortLinkGotoMapper.update(shortLinkGotoDO, shortLinkGotoUpdateWrapper);
        }


    }


    private String generateSuffix(ShortLinkCreateReqDTO requestParam) {
        String shortUri;
        int customGenerateCount = 0;
        while (true) {
            if (customGenerateCount > 10) {
                throw new ServiceException("短链接频繁生成 ，请稍后再试");
            }
            String originUrl = requestParam.getOriginUrl();
            originUrl += System.currentTimeMillis();
            shortUri = HashUtil.hashToBase62(originUrl);
            String fullShortUrl = requestParam.getDomain() + "/" + shortUri;
            if (!shortUriCreateCachePenetrationBloomFilter.contains(fullShortUrl)) {
                break;
            }
            customGenerateCount++;
        }
        return shortUri;
    }

    private static String getIcon(String url) {
        try {
            // 使用Jsoup从URL获取HTML文档
            Document document = Jsoup.connect(url).get();

            // 查找所有的link元素
            Elements linkElements = document.select("link");

            // 遍历link元素，查找包含图标信息的元素
            for (Element linkElement : linkElements) {
                String rel = linkElement.attr("rel");
                if (rel.contains("icon")) {
                    // 获取图标的URL
                    String iconUrl = linkElement.attr("href");
                    return iconUrl;
                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(DateUtil.dayOfMonth(new Date()));
    }

}
