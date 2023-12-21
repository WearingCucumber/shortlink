package com.study.shortLink.admin.controller;

import com.study.shortLink.admin.common.convention.result.Result;
import com.study.shortLink.admin.common.convention.result.Results;
import com.study.shortLink.admin.dto.req.UserLoginReqDTO;
import com.study.shortLink.admin.dto.req.UserRegisterReqDTO;
import com.study.shortLink.admin.dto.req.UserUpdateReqDTO;
import com.study.shortLink.admin.dto.resp.UserLoginRespDTO;
import com.study.shortLink.admin.dto.resp.UserRespDTO;
import com.study.shortLink.admin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理控制层
 */
@RestController
//@RequiredArgsConstructor
public class UserController {
    @Autowired
    private  UserService userService;
    /**
     *  根据用户名查询用户信息
     * @param username
     * @return
     */
    @GetMapping("/api/short-link/admin/v1/user/{username}")
    public Result<UserRespDTO> getUserByUsername(@PathVariable("username") String username){
        UserRespDTO result = userService.getUserByUsername(username);
        return Results.success(result);
    }

    /**
     * 查询用户名是否存在
     * @param username
     * @return
     */
    @GetMapping("/api/short-link/admin/v1/user/has-username")
    public Result<Boolean> hasUsername(@RequestParam("username") String username){
        System.out.println("111111111");
        return Results.success(userService.hasUsername(username));
    }

    /**
     * 用户注册
     * @param requestParam
     * @return
     */
    @PostMapping("/api/short-link/admin/v1/user")
    public Result<Void> register (@RequestBody UserRegisterReqDTO requestParam){
        userService.register(requestParam);
        return Results.success();
    }

    /**
     * 修改用户
     * @param requestParam
     * @return
     */
    @PutMapping("/api/short-link/admin/v1/user")
    public Result<Void> update(@RequestBody UserUpdateReqDTO requestParam){
        userService.update(requestParam);
        return Results.success();
    }

    /**
     * 用户登录
     * @param requestParam
     * @return
     */
    @PostMapping("/api/short-link/admin/v1/user/login")
    public Result<UserLoginRespDTO> login(@RequestBody UserLoginReqDTO requestParam){
        UserLoginRespDTO result = userService.login(requestParam);
        return Results.success(result);
    }

    /**
     * 检查用户是否登录
     * @param token
     * @return
     */
    @GetMapping("/api/short-link/admin/v1/user/check-login")
    public Result<Boolean> checkLogin(@RequestParam("username")String username,@RequestParam("token")String token){
        Boolean res = userService.checkLogin( username,token);
        return Results.success(res);
    }

    /**
     * 用户退出登录
     * @return
     */
    @DeleteMapping("/api/short-link/admin/v1/user/logout")
    public Result<Void> logout(){
        userService.logout();
        return Results.success();
    }

}
