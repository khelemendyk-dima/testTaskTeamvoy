package com.my.shop.services.impl;

import com.my.shop.exceptions.ProductAlreadyExistsException;
import com.my.shop.models.Product;
import com.my.shop.repositories.ProductRepository;
import com.my.shop.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    @Transactional
    @Override
    public Product save(Product product) {
        Optional<Product> foundProduct = productRepository.findByName(product.getName());

        if (foundProduct.isPresent()) {
            throw new ProductAlreadyExistsException(String.format("Product with the name '%s' is already in use", product.getName()));
        }

        return productRepository.save(product);
    }

    @Override
    public List<Product> getAll() {
        return productRepository.findAll();
    }
}
