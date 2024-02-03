package com.study.shortLink.admin.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.study.shortLink.admin.common.biz.user.UserContext;
import com.study.shortLink.admin.common.convention.exception.ServiceException;
import com.study.shortLink.admin.common.convention.result.Result;
import com.study.shortLink.admin.dao.entity.GroupDO;
import com.study.shortLink.admin.dao.mapper.GroupMapper;
import com.study.shortLink.admin.remote.ShortLinkActualRemoteService;
import com.study.shortLink.admin.remote.dto.req.RecycleBinPageReqDTO;
import com.study.shortLink.admin.remote.dto.resp.ShortLinkPageRespDTO;
import com.study.shortLink.admin.service.RecycleBinService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecycleBinServiceImpl implements RecycleBinService {
    private final GroupMapper groupMapper;
    private final ShortLinkActualRemoteService shortLinkActualRemoteService;

    @Override
    public Result<Page<ShortLinkPageRespDTO>> recycleBinPage(RecycleBinPageReqDTO requestParam) {
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .eq(GroupDO::getDelFlag, 0);
        List<GroupDO> groupDOList = groupMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(groupDOList)) {
            throw new ServiceException("用户无分组信息");
        }
        List<String> gidList = groupDOList.stream().map(GroupDO::getGid).toList();
        requestParam.setGidList(gidList);
        return shortLinkActualRemoteService.pageRecycleBinShortLink(requestParam.getGidList(),requestParam.getCurrent(),requestParam.getSize());
    }
}
