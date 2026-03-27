package org.example.ec.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("users")
public class User {
    private Integer id;
    private String avatar;
    private String username;
    private String password;
    private String role;
    private String email;
    private String bio;       // 个性签名
    private String location;  // 地区
}
