package org.example.ec.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("products")
public class Products {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private int userId;
    private String productname;
    private Date orderTime;
    private String address;
    private double price;
    private int quantity;
}
