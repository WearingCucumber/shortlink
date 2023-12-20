package com.study.shortLink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.study.shortLink.admin.common.biz.user.UserContext;
import com.study.shortLink.admin.common.convention.exception.ClientException;
import com.study.shortLink.admin.dao.entity.GroupDO;
import com.study.shortLink.admin.dao.mapper.GroupMapper;
import com.study.shortLink.admin.dto.resp.ShortLinkGroupRespDTO;
import com.study.shortLink.admin.service.GroupService;
import com.study.shortLink.admin.toolkit.RandomStringGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class GroupServiceImpl  extends ServiceImpl<GroupMapper, GroupDO> implements GroupService {
    @Override
    public void saveGroup(String groupName) {
        String username = UserContext.getUsername();
        /**
         * 此处用来判断当前用户下是否有相同的组名称了  如果有则 报错
         */
        LambdaQueryWrapper<GroupDO> hasSameGroupNameDo = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getName, groupName)
                .eq(GroupDO::getUsername, username);
        GroupDO hasSameGroupNameFlag = baseMapper.selectOne(hasSameGroupNameDo);
        if (hasSameGroupNameFlag!=null){
            throw new ClientException("已经有相同的组名称");
        }
        String gid;
        do {
            gid = RandomStringGenerator.generateRandom();
        } while (!hasGid(gid));
        GroupDO groupDO = GroupDO.builder()
                .username(username)
                .name(groupName)
                .gid(RandomStringGenerator.generateRandom())
                .sortOrder(0)
                .build();
        baseMapper.insert(groupDO);
    }

    @Override
    public List<ShortLinkGroupRespDTO> listGroup() {
        String username = UserContext.getUsername();
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getDelFlag, 0)
                .eq(GroupDO::getUsername,username )
                .orderByDesc(GroupDO::getSortOrder, GroupDO::getUpdateTime);
        List<GroupDO> groupDOList = baseMapper.selectList(queryWrapper);
        return BeanUtil.copyToList(groupDOList, ShortLinkGroupRespDTO.class);
    }

    private  boolean hasGid(String gid){
        String username = UserContext.getUsername();
        LambdaQueryWrapper<GroupDO> wrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getGid, gid)
                .eq(GroupDO::getUsername, username)
                .or();
        GroupDO hasGroupFlag = baseMapper.selectOne(wrapper);
        return hasGroupFlag==null;
    }
}
