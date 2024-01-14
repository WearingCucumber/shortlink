package com.study.shortLink.project.dto.req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import java.util.List;

/**
 * 回收站分页查询入参
 */
@Data
public class RecycleBinPageReqDTO extends Page {
    /**
     * 分组标识
     */
    private List<String> gidList;
}