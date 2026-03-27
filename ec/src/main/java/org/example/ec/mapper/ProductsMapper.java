package org.example.ec.mapper;

import org.apache.ibatis.annotations.*;
import org.example.ec.DTO.ProductSearchDTO;
import org.example.ec.entity.Products;

import java.util.List;

@Mapper
public interface ProductsMapper{
    //使用mybatis进行sql操作
    // 根据 userId 查询
    @Select("select * from products where userId = #{userId}")
    List<Products> getProductsByUserId(Integer userId);

    //插入商品
    @Insert("insert into products(productname,price,address,orderTime,quantity,userId) values (#{productname},#{price},#{address},#{orderTime},#{quantity},#{userId})")
    int addProduct(Products product);

    //删除
    @Delete("delete from products where id = #{id}")
    int deleteProduct(int id);

    //更新
    @Update("update products set productname=#{productname},price=#{price},quantity=#{quantity},address=#{address},orderTime=#{orderTime} where id = #{id}")
    int updateProduct(Products product);

    //搜索
    @Select({
            "<script>",
            "SELECT * FROM products",
            "<where>",
            "<if test='searchProductName != null and searchProductName != \"\"'>AND productname LIKE concat('%', #{searchProductName}, '%')</if>",
            "<if test='searchProductPrice != null and searchProductPrice != \"\"'>",
            "AND price BETWEEN ",
            "<choose>",
            "<when test='searchProductPrice == \"0-100\"'>0 AND 100</when>",
            "<when test='searchProductPrice == \"100-500\"'>100 AND 500</when>",
            "<when test='searchProductPrice == \"500-1000\"'>500 AND 1000</when>",
            "<otherwise>0 AND 1000000</otherwise>",  //默认设置一个较大的范围，防止为空时出错
            "</choose>",
            "</if>",
            "</where>",
            "<if test='sortOrder != null and sortOrder != \"\"'>ORDER BY price ${sortOrder}</if>",  // 根据价格排序，升序或降序
            "<if test='sortOrder == \"\"'>",
            "ORDER BY id",  // 默认按 id 排序
            "</if>",
            "</script>"
    })
    List<Products> searchProducts(ProductSearchDTO searchDTO);
}
