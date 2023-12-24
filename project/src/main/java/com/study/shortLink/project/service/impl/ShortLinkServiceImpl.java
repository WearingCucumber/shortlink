package com.study.shortLink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.study.shortLink.project.common.convention.exception.ServiceException;
import com.study.shortLink.project.dao.entity.ShortLinkDO;
import com.study.shortLink.project.dao.mapper.LinkMapper;
import com.study.shortLink.project.dto.req.ShortLinkCreateReqDTO;
import com.study.shortLink.project.dto.req.ShortLinkPageReqDTO;
import com.study.shortLink.project.dto.resp.ShortLinkCreateRespDTO;
import com.study.shortLink.project.dto.resp.ShortLinkPageRespDTO;
import com.study.shortLink.project.service.ShortLinkService;
import com.study.shortLink.project.toolkit.HashUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

/**
 * 短链接接口实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ShortLinkServiceImpl extends ServiceImpl<LinkMapper, ShortLinkDO> implements ShortLinkService {
    private final RBloomFilter<String> shortUriCreateCachePenetrationBloomFilter;

    @Override
    public ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam) {
        String shortLinkSuffix = generateSuffix(requestParam);
        String fullShortUrl = requestParam.getDomain() + "/" + shortLinkSuffix;
        ShortLinkDO shortLinkDO = BeanUtil.toBean(requestParam, ShortLinkDO.class);
        shortLinkDO.setFullShortUrl(fullShortUrl);
        shortLinkDO.setShortUri(shortLinkSuffix);
        try {
            baseMapper.insert(shortLinkDO);
        } catch (DuplicateKeyException ex) {
/*            LambdaQueryWrapper<ShortLinkDO> wrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                    .eq(ShortLinkDO::getFullShortUrl, fullShortUrl);
            Long count = baseMapper.selectCount(wrapper);
            if (count != 0) {
            }
          */
            log.warn("短链接：{}", "重复入库", fullShortUrl);
            shortUriCreateCachePenetrationBloomFilter.add(fullShortUrl);
            throw new ServiceException("短链接生成重复");
        }
        shortUriCreateCachePenetrationBloomFilter.add(fullShortUrl);
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
                .eq(ShortLinkDO::getEnableStatus, 0);
        IPage<ShortLinkDO> page  = baseMapper.selectPage(requestParam, wrapper);
        return page.convert(each -> BeanUtil.toBean(each, ShortLinkPageRespDTO.class));
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
}
