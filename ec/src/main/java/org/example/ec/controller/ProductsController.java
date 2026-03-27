package org.example.ec.controller;

import common.Result;
import org.example.ec.DTO.AiRequsetDTO;
import org.example.ec.DTO.ProductSearchDTO;
import org.example.ec.entity.Products;
import org.example.ec.service.AiService;
import org.example.ec.service.ProductsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ProductsController {
    @Autowired
    private ProductsService productsService;

    @Autowired
    private AiService aiService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @GetMapping("/products")
    public Result<List<Products>> getProducts(HttpServletRequest request) {
        // 从请求头取 token
        String token = request.getHeader("Authorization");
        // 去 Redis 查 userId
        String userIdStr = redisTemplate.opsForValue().get("token:" + token);
        if (userIdStr == null) {
            return Result.error("登录过期");
        }
        Integer userId = Integer.parseInt(userIdStr);

        List<Products> result = productsService.fetchProducts(userId);
        return Result.success("获取数据成功",result);
    }

    @PostMapping("/add")
    public Result<Products> addProduct(@RequestBody Products product, HttpServletRequest request) {
        // 从 token 解析 userId
        String token = request.getHeader("Authorization");
        String userIdStr = redisTemplate.opsForValue().get("token" +
                ":" + token);
        if (userIdStr == null) {
            return Result.error("登录过期");
        }
        Integer userId = Integer.parseInt(userIdStr);

        // ✅ 把 userId 塞进 product 再存
        product.setUserId(userId);

        productsService.insertProduct(product);
        return Result.success("添加成功", product);
    }

    @PostMapping("/remove/{id}")
    public Result<Void> deleteProduct(@PathVariable int id) {
        productsService.removeProduct(id);
        return Result.success("删除成功");
    }

    @PostMapping("/update")
    public Result<Products> updateProduct(@RequestBody Products product) {
        productsService.saveProduct(product);
        return Result.success("更新成功",product);
    }

    @PostMapping("/search")
    public Result<List<Products>> searchProduct(@RequestBody ProductSearchDTO searchDTO) {
        List<Products> result = productsService.findProducts(searchDTO);
        return Result.success("搜索商品成功",result);
    }

    @PostMapping("ai/chat")
    public Result<String> chat(@RequestBody AiRequsetDTO request){
        String reply = aiService.chat(request.getMessage());
        return Result.success("成功",reply);
    }
}
