package org.example.ec.controller;

import common.Result;
import org.example.ec.DTO.EmailDTO;
import org.example.ec.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;


@RestController
@RequestMapping("/api")
public class AuthController {
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result<?> login(@RequestBody Map<String, String> map) {
        return userService.findAll(
                map.get("username"),
                map.get("password")
        );
    }

    @PostMapping("/sendCode")
    public Result<?> send(@RequestBody @Valid EmailDTO dto) {
        userService.sendEmail(dto);
        return Result.success("验证码已发送,5分钟内有效！");
    }

    @PostMapping("/register")
    public Result<?> register(@RequestBody @Valid EmailDTO dto) {
        userService.userRegister(dto);
        return Result.success("注册成功");
    }
}
