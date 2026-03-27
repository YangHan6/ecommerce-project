package org.example.ec.service;

import org.example.ec.entity.Goods;
import org.example.ec.mapper.GoodsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class GoodsService {
    @Autowired
    private GoodsMapper goodsMapper;

    public List<Goods> showAll() {
        List<Goods> goods = goodsMapper.getGoods();
        return goods;
    }

    public void upGoods(MultipartFile file, String name, Double price, String description) throws IOException {
        // 先保存图片，拿到 URL
        String ext = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        String filename = UUID.randomUUID().toString().replace("-", "") + ext;
        File dest = new File("C:/Users/20997/Desktop/电商平台系统/images/" + filename);
        dest.getParentFile().mkdirs();
        file.transferTo(dest);
        String imageUrl = "/images/" + filename;

        // 再存数据库
        Goods goods = new Goods();
        goods.setName(name);
        goods.setPrice(price);
        goods.setDescription(description);
        goods.setImage(imageUrl);

        int result = goodsMapper.insertGoods(goods);
        if (result == 0) {
            throw new RuntimeException("上传失败");
        }
    }
}
