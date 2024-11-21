package org.myproject.shortlink.admin.controller;

import cn.hutool.core.bean.BeanUtil;
import lombok.RequiredArgsConstructor;
import org.myproject.shortlink.admin.common.convention.result.Result;
import org.myproject.shortlink.admin.common.convention.result.Results;
import org.myproject.shortlink.admin.dto.req.UserLoginReqDTO;
import org.myproject.shortlink.admin.dto.req.UserRegisterReqDTO;
import org.myproject.shortlink.admin.dto.req.UserUpdateReqDTO;
import org.myproject.shortlink.admin.dto.resp.UserActualRespDTO;
import org.myproject.shortlink.admin.dto.resp.UserLoginRespDTO;
import org.myproject.shortlink.admin.dto.resp.UserRespDTO;
import org.myproject.shortlink.admin.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    @GetMapping("/api/shortlink/admin/v1/user/{username}")
    public Result<UserRespDTO> getUserByUsername(@PathVariable("username") String username) {
        return Results.success(userService.getUserByUsername(username));
    }

    @GetMapping("/api/shortlink/admin/v1/actual/user/{username}")
    public Result<UserActualRespDTO> getUserActualByUsername(@PathVariable("username") String username) {
        return Results.success(BeanUtil.toBean(userService.getUserByUsername(username), UserActualRespDTO.class));
    }
    @GetMapping("/api/shortlink/admin/v1/user/has-username")
    public Result<Boolean> hasUsername(@RequestParam("username") String username) {
        System.out.println(username);
        return Results.success(userService.hasUsername(username));
    }

    @PostMapping("/api/shortlink/admin/v1/user/register")
    public Result<Void> register(@RequestBody UserRegisterReqDTO registerParam) {
        userService.register(registerParam);
        return Results.success();
    }

    @PutMapping("/api/shortlink/admin/v1/user/update")
    public Result<Void> update(@RequestBody UserUpdateReqDTO updateParam) {
        userService.update(updateParam);
        return Results.success();
    }

    @PostMapping("/api/shortlink/admin/v1/user/login")
    public Result<UserLoginRespDTO> login(@RequestBody UserLoginReqDTO loginParam) {
        UserLoginRespDTO login = userService.login(loginParam);
        return Results.success(login);
    }

    @GetMapping("/api/shortlink/admin/v1/user/check-login")
    public Result<Boolean> checkLogin(@RequestParam("username") String username, @RequestParam("token") String token) {
        return Results.success(userService.checkLogin(username, token));
    }

    @DeleteMapping("/api/shortlink/admin/v1/user/logout")
    public Result<Void> logout(@RequestParam("username") String username, @RequestParam("token") String token) {
        userService.logout(username, token);
        return Results.success();
    }
}
