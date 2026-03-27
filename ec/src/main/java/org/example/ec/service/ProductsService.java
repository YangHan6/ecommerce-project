package org.example.ec.service;

import org.example.ec.DTO.ProductSearchDTO;
import org.example.ec.entity.Products;
import org.example.ec.mapper.ProductsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductsService {
    @Autowired
    private ProductsMapper productsMapper;

    //查询商品
    public List<Products> fetchProducts(Integer userId){
        List<Products> product = productsMapper.getProductsByUserId(userId);
        return product;
    }

    //添加商品，前端用的是code判断
    public void insertProduct(Products product){
        int result = productsMapper.addProduct(product);
        if(result == 0){
            throw new RuntimeException("添加失败");
        }
    }

    //删除商品
    public void removeProduct(int id) {
        int result = productsMapper.deleteProduct(id);
        if(result == 0){
            throw new RuntimeException("商品不存在,删除失败");
        }
    }

    //更新商品
    public void saveProduct(Products product){
        int result = productsMapper.updateProduct(product);
        if(result == 0){
            throw new RuntimeException("更新失败");
        }
    }

    //搜索
    public List<Products> findProducts(ProductSearchDTO searchDTO){
        List<Products> products = productsMapper.searchProducts(searchDTO);
        return products;
    }

}
