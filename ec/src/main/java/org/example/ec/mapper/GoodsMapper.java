package org.example.ec.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.ec.entity.Goods;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Mapper
public interface GoodsMapper {
    @Select("select * from goods")
    List<Goods> getGoods();

    @Insert("insert into goods(name, price, image, description) values(#{name}, #{price}, #{image}, #{description})")
    int insertGoods(Goods goods);
}
