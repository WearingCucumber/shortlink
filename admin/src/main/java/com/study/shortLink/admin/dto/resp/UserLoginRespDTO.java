package com.study.shortLink.admin.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 用户登录接口返回响应
 */
@AllArgsConstructor
@Data
public class UserLoginRespDTO {
    /**
     * 用户token
     */
    private String token ;
}
