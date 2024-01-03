package com.study.shortLink.admin.remote.dto;

import lombok.Data;

@Data
public class ShortLinkGroupCountQueryRespDTO {
    private String gid;
    private Integer shortLinkCount;

}
