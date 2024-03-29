package com.study.shortLink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.study.shortLink.admin.common.biz.user.UserContext;
import com.study.shortLink.admin.common.convention.exception.ClientException;
import com.study.shortLink.admin.common.convention.result.Result;
import com.study.shortLink.admin.dao.entity.GroupDO;
import com.study.shortLink.admin.dao.mapper.GroupMapper;
import com.study.shortLink.admin.dto.req.ShortLinkGroupSortGroupReqDTO;
import com.study.shortLink.admin.dto.req.ShortLinkGroupUpdateGroupReqDTO;
import com.study.shortLink.admin.dto.resp.ShortLinkGroupRespDTO;
import com.study.shortLink.admin.remote.ShortLinkActualRemoteService;
import com.study.shortLink.admin.remote.dto.resp.ShortLinkGroupCountQueryRespDTO;
import com.study.shortLink.admin.service.GroupService;
import com.study.shortLink.admin.toolkit.RandomStringGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO> implements GroupService {
    private final ShortLinkActualRemoteService shortLinkActualRemoteService;
    @Override
    public void saveGroup(String groupName) {
        String username = UserContext.getUsername();
        /*
         * 此处用来判断当前用户下是否有相同的组名称了  如果有则 报错
         */
        LambdaQueryWrapper<GroupDO> hasSameGroupNameDo = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getName, groupName)
                .eq(GroupDO::getUsername, username);
        GroupDO hasSameGroupNameFlag = baseMapper.selectOne(hasSameGroupNameDo);
        if (hasSameGroupNameFlag != null) {
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
                .eq(GroupDO::getUsername, username)
                .orderByDesc(GroupDO::getSortOrder, GroupDO::getUpdateTime);
        List<GroupDO> groupDOList = baseMapper.selectList(queryWrapper);
        Result<List<ShortLinkGroupCountQueryRespDTO>> listResult = shortLinkActualRemoteService
                .listGroupShortLinkCount(groupDOList.stream().map(GroupDO::getGid).toList());
        List<ShortLinkGroupRespDTO> shortLinkGroupRespDTOS = BeanUtil.copyToList(groupDOList, ShortLinkGroupRespDTO.class);
        shortLinkGroupRespDTOS.forEach(each ->{
            Optional<ShortLinkGroupCountQueryRespDTO> first  = listResult.getData().stream().filter(item -> Objects.equals(item.getGid(), each.getGid())).findFirst();
            first.ifPresent(item ->each.setShortLinkCount(first.get().getShortLinkCount()));
        });
        return shortLinkGroupRespDTOS;
    }

    @Override
    public void updateGroup(ShortLinkGroupUpdateGroupReqDTO requestParam) {
        String username = UserContext.getUsername();
        LambdaUpdateWrapper<GroupDO> updateWrapper = Wrappers.lambdaUpdate(GroupDO.class)
                .eq(GroupDO::getUsername, username)
                .eq(GroupDO::getGid, requestParam.getGid())
                .eq(GroupDO::getDelFlag, 0);
        GroupDO groupDO = GroupDO.builder()
                .name(requestParam.getName())
                .build();
        baseMapper.update(groupDO, updateWrapper);
    }

    @Override
    public void deleteGroup(String gid) {
        String username = UserContext.getUsername();
        LambdaUpdateWrapper<GroupDO> updateWrapper = Wrappers.lambdaUpdate(GroupDO.class)
                .eq(GroupDO::getUsername, username)
                .eq(GroupDO::getGid, gid)
                .eq(GroupDO::getDelFlag, 0);
        GroupDO groupDO = new GroupDO();
        groupDO.setDelFlag(1);
        baseMapper.update(groupDO, updateWrapper);

    }

    @Override
    public void sortGroup(List<ShortLinkGroupSortGroupReqDTO> requestParam) {
        String username = UserContext.getUsername();
        requestParam.forEach(each -> {
            GroupDO groupDO = GroupDO.builder()
                    .sortOrder(each.getSortOrder())
                    .build();
            LambdaUpdateWrapper<GroupDO> updateWrapper = Wrappers.lambdaUpdate(GroupDO.class)
                    .eq(GroupDO::getUsername, username)
                    .eq(GroupDO::getGid, each.getGid())
                    .eq(GroupDO::getDelFlag, 0);
            baseMapper.update(groupDO, updateWrapper);
        });

    }

    @Override
    public void addGroupAfterUserRegister(String username) {
        GroupDO groupDO = GroupDO.builder()
                .username(username)
                .name("默认分组")
                .gid(RandomStringGenerator.generateRandom())
                .sortOrder(0)
                .build();
        baseMapper.insert(groupDO);
    }

    private boolean hasGid(String gid) {
        String username = UserContext.getUsername();
        LambdaQueryWrapper<GroupDO> wrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getGid, gid)
                .eq(GroupDO::getUsername, username)
                .or();
        GroupDO hasGroupFlag = baseMapper.selectOne(wrapper);
        return hasGroupFlag == null;
    }
}
