package com.my.shop.services;

import com.my.shop.models.Product;

import java.util.List;

public interface ProductService {
    Product save(Product product);
    List<Product> getAll();
    Product findById(Long productId);
}
