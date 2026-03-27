package org.example.ec.service;

import common.Result;
import org.example.ec.DTO.EmailDTO;
import org.example.ec.DTO.UpdatePassword;
import org.example.ec.entity.User;
import org.example.ec.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    public Result<?> findAll(String username, String password) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            return Result.error("用户不存在");
        }
        if (!user.getPassword().equals(password)) {
            return Result.error("密码错误");
        }

        String token = UUID.randomUUID().toString().replace("-", "");   //生成一个token
        String redisKey ="token:"+ token;  //前缀是为了区分redis里的随机字符串（区分token或者商品缓存）
        // 存入 Redis，过期时间 2小时加2~5分钟，避免缓存雪崩
        long baseTime = 2*60*60;
        long randomTime = 120 + new Random().nextInt(181);                    // 120~300秒
        long expireTime = baseTime + randomTime;
        redisTemplate.opsForValue().set(redisKey, String.valueOf(user.getId()), expireTime, TimeUnit.SECONDS);

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("username", user.getUsername());
        data.put("role", user.getRole());
        data.put("avatar", user.getAvatar());
        data.put("email", user.getEmail());
        return Result.success("登录成功",data);
    }

    public void userRegister(EmailDTO dto) {
        if (userMapper.findByUsername(dto.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }
        String realCode = redisTemplate.opsForValue().get("captcha:" + dto.getEmail());
        if (realCode == null) {
            throw new RuntimeException("无效验证码");
        }
        if (!realCode.equals(dto.getVerifyCode())){
            throw new RuntimeException("验证码错误");
        }
        int result = userMapper.InsertUser(dto.getUsername(),dto.getPassword());
        if (result ==0) {
            throw new RuntimeException("注册失败");
        }
    }

    public void sendEmail(EmailDTO dto) {
        if (userMapper.findByUsername(dto.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }
        //生成6位数验证码
        String code = String.valueOf(100000 + new Random().nextInt(900000));
        // 存Redis（5分钟过期）
        redisTemplate.opsForValue()
                .set("captcha:" + dto.getEmail(), code, 5, TimeUnit.MINUTES);
        // 发邮件
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(dto.getEmail());
        message.setSubject("验证码");
        message.setText("【电商平台】您的验证码是：" + code + "，5分钟内有效");

        mailSender.send(message);

    }

    //更新密码
    public void updataPassword(UpdatePassword dto, Long userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!user.getPassword().equals(dto.getOldPassword())) {
            throw new RuntimeException("原密码不正确");
        }
        userMapper.updatePassword(userId,dto.getNewPassword());
    }

    // 发送绑定邮箱的验证码
    public void sendBindEmailCode(String email) {
        String code = String.valueOf(100000 + new Random().nextInt(900000));
        redisTemplate.opsForValue().set("bindEmail:" + email, code, 5, TimeUnit.MINUTES);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(email);
        message.setSubject("绑定邮箱验证码");
        message.setText("【电商平台】您的验证码是：" + code + "，5分钟内有效");
        mailSender.send(message);
    }

    // 验证验证码并更新邮箱
    public void updateEmail(String email, String code, Long userId) {
        String realCode = redisTemplate.opsForValue().get("bindEmail:" + email);
        if (realCode == null) {
            throw new RuntimeException("验证码已过期");
        }
        if (!realCode.equals(code)) {
            throw new RuntimeException("验证码错误");
        }
        userMapper.updateEmail(userId, email);
        redisTemplate.delete("bindEmail:" + email);
    }

    public void updateProfile(String bio, String location, Long userId) {
        userMapper.updateProfile(userId, bio, location);
    }
}
