package com.study.shortLink.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.study.shortLink.admin.dao.entity.GroupDO;
import com.study.shortLink.admin.dao.mapper.GroupMapper;
import com.study.shortLink.admin.service.GroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
@Slf4j
@RequiredArgsConstructor
@Service
public class GroupServiceImpl  extends ServiceImpl<GroupMapper, GroupDO> implements GroupService {
}
