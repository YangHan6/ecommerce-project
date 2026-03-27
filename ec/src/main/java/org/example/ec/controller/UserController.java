package org.example.ec.controller;

import common.Result;
import org.example.ec.DTO.UpdatePassword;
import org.example.ec.service.UserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {
    private final StringRedisTemplate redisTemplate;
    private final UserService userService;

    public UserController(@Qualifier("stringRedisTemplate") StringRedisTemplate redisTemplate, UserService userService) {
        this.redisTemplate = redisTemplate;
        this.userService = userService;
    }

    @PostMapping("/user/updatePassword")
    public Result<?> updatePassword(@RequestBody UpdatePassword dto, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        String userId0 = redisTemplate.opsForValue().get("token:" + token);
        if (userId0 == null) {
            return Result.error("登录已过期");
        }
        Long userId = Long.valueOf(userId0);
        userService.updataPassword(dto,userId);
        return Result.success("已更改密码");
    }

    @PostMapping("/user/sendBindEmailCode")
    public Result<?> sendBindEmailCode(@RequestBody Map<String, String> body) {
        userService.sendBindEmailCode(body.get("email"));
        return Result.success("验证码发送成功");
    }

    @PostMapping("/user/updateEmail")
    public Result<?> updateEmail(@RequestBody Map<String, String> body, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        String userIdStr = redisTemplate.opsForValue().get("token:" + token);
        if (userIdStr == null) return Result.error("登录已过期");
        userService.updateEmail(body.get("email"), body.get("code"), Long.valueOf(userIdStr));
        return Result.success("邮箱绑定成功");
    }

    @PostMapping("/user/updateProfile")
    public Result<?> updateProfile(@RequestBody Map<String, String> body, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        String userIdStr = redisTemplate.opsForValue().get("token:" + token);
        if (userIdStr == null) return Result.error("登录已过期");
        userService.updateProfile(body.get("bio"), body.get("location"), Long.valueOf(userIdStr));
        return Result.success("保存成功");
    }
}
