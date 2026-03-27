package org.example.ec.controller;

import common.Result;
import org.example.ec.entity.Goods;
import org.example.ec.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class GoodsController {
    @Autowired
    private GoodsService goodsService;

    @GetMapping("/goods")
    public Result<List<Goods>> findAll() {
        List<Goods> goods = goodsService.showAll();
        return Result.success("商品显示成功",goods);
    }

    @PostMapping("/goods/add")
    public Result<?> addGoods(
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") String name,
            @RequestParam("price") Double price,
            @RequestParam("description") String description) throws IOException {
        goodsService.upGoods(file, name, price, description);
        return Result.success("上架成功", null);
    }
}
