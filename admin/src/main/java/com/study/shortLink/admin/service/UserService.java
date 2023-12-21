package com.study.shortLink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.study.shortLink.admin.dao.entity.UserDO;
import com.study.shortLink.admin.dto.req.UserLoginReqDTO;
import com.study.shortLink.admin.dto.req.UserRegisterReqDTO;
import com.study.shortLink.admin.dto.req.UserUpdateReqDTO;
import com.study.shortLink.admin.dto.resp.UserLoginRespDTO;
import com.study.shortLink.admin.dto.resp.UserRespDTO;

/**
 * 用户接口层
 */
public interface UserService extends IService<UserDO> {
        /**
         * 根据用户名查询用户信息
         * @param username
         * @return
         */
        UserRespDTO getUserByUsername(String username);

        /**
         * 查询用户名是否存在
         * @param username
         * @return 用户名存在true 不存在false
         */
        Boolean hasUsername(String username);

        /**
         * 注册用户
         * @param requestParam 注册用户请求参数
         */
        void register(UserRegisterReqDTO requestParam);

        /**
         * 根据用户名修改用户信息
         * @param requestParam 修改用户请求参数
         */
        void update(UserUpdateReqDTO requestParam);

        /**
         * 用户登录
         * @param requestParam 用户登录请求参数
         * @return UserLoginRespDTO 用户登录返回参数
         */

        UserLoginRespDTO login(UserLoginReqDTO requestParam);

        /**
         * 检查用户是否登录
         * @param token
         * @return
         */
        Boolean checkLogin(String username , String token);

        /**
         * 退出登录
         */

        void logout();
}
