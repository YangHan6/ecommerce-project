package org.example.ec.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("messages")
public class Messages {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer channelId;
    private String author;
    private String content;
    private String color;
    private LocalDateTime createTime;
}
