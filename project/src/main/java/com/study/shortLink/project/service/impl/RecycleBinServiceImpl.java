package com.study.shortLink.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.study.shortLink.project.dao.entity.ShortLinkDO;
import com.study.shortLink.project.dao.mapper.ShortLinkMapper;
import com.study.shortLink.project.dto.req.RecycleBinSaveReqDTO;
import com.study.shortLink.project.service.RecycleBinService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;

import static com.study.shortLink.project.common.constant.RedisKeyConstant.GOTO_SHORT_LINK_KEY;

/**
 * 回收站接口实现类
 */
@Service
@RequiredArgsConstructor
public class RecycleBinServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements RecycleBinService {
    private final StringRedisTemplate stringRedisTemplate;


    @Override
    public void saveRecycleBin(RecycleBinSaveReqDTO requestParam) {
        String shortLinkSuffix;
        try {
            URL url = new URL(requestParam.getFullShortUrl());
            shortLinkSuffix = url.getPath().substring(1);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        LambdaUpdateWrapper<ShortLinkDO> updateWrapper = Wrappers.lambdaUpdate(ShortLinkDO.class)
                .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                .eq(ShortLinkDO::getGid, requestParam.getGid())
                .eq(ShortLinkDO::getEnableStatus, 0)
                .eq(ShortLinkDO::getDelFlag, 0);
        ShortLinkDO linkDO = ShortLinkDO.builder()
                .enableStatus(1).build();
        baseMapper.update(linkDO,updateWrapper);
        stringRedisTemplate.delete(
                String.format(GOTO_SHORT_LINK_KEY, shortLinkSuffix)
        );


    }

    public static void main(String[] args) throws MalformedURLException {
        URL url = new URL("http://uri.link/VyWdf");
        System.out.println(url.getPath().substring(1));
    }
}
