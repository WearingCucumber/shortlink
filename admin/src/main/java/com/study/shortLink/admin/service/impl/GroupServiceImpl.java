package com.study.shortLink.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.study.shortLink.admin.dao.entity.GroupDO;
import com.study.shortLink.admin.dao.mapper.GroupMapper;
import com.study.shortLink.admin.service.GroupService;
import com.study.shortLink.admin.toolkit.RandomStringGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
@Slf4j
@RequiredArgsConstructor
@Service
public class GroupServiceImpl  extends ServiceImpl<GroupMapper, GroupDO> implements GroupService {
    @Override
    public void saveGroup(String groupName) {
        String gid;
        do {
            gid = RandomStringGenerator.generateRandom();
        } while (!hasGid(gid));
        GroupDO groupDO = GroupDO.builder()
                .name(groupName)
                .gid(RandomStringGenerator.generateRandom())
                .build();
        baseMapper.insert(groupDO);
    }
    private  boolean hasGid(String gid){

        LambdaQueryWrapper<GroupDO> wrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getGid, gid)
                //TODO 设置用户名
                .eq(GroupDO::getUsername, null);
        GroupDO hasGroupFlag = baseMapper.selectOne(wrapper);
        return hasGroupFlag==null;
    }
}
