package com.study.shortLink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.study.shortLink.admin.dao.entity.GroupDO;
import com.study.shortLink.admin.dto.req.ShortLinkGroupSortGroupReqDTO;
import com.study.shortLink.admin.dto.req.ShortLinkGroupUpdateGroupReqDTO;
import com.study.shortLink.admin.dto.resp.ShortLinkGroupRespDTO;

import java.util.List;

/**
 * 分组接口层
 */
public interface GroupService extends   IService<GroupDO> {
    /**
     * 新增短链接分组
     * @param groupName 短链接分组名
     */
    void saveGroup(String groupName);

    /**
     * 查询用户短链接分组集合
     * @return
     */
    List<ShortLinkGroupRespDTO> listGroup();

    /**
     * 修改短链接分组名称
     * @param requestParam 请求参数
     */
    void updateGroup(ShortLinkGroupUpdateGroupReqDTO requestParam);

    /**
     * 删除分组
     * @param gid
     */
    void deleteGroup(String gid);

    /**
     * 短链接分组排序
     * @param requestParam
     */
    void sortGroup(List<ShortLinkGroupSortGroupReqDTO> requestParam);

    /**
     * 用户注册后添加默认分组
     * @param username  用户名
     */
    void addGroupAfterUserRegister(String username);
}
