package com.my.shop.controllers;

import com.my.shop.dtos.ProductDTO;
import com.my.shop.models.Product;
import com.my.shop.services.ProductService;
import com.my.shop.utils.ConvertorUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/products")
public class ProductController {
    private final ProductService productService;
    private final ConvertorUtil convertor;

    @PostMapping()
    public ResponseEntity<ProductDTO> saveProduct(@RequestBody @Valid ProductDTO productDTO) {
        Product product = productService.save(convertor.convertToProduct(productDTO));

        return ResponseEntity.ok(convertor.convertToProductDTO(product));
    }

    @GetMapping()
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<ProductDTO> productDTOS = convertor.convertListToProductDTO(productService.getAll());

        return ResponseEntity.ok(productDTOS);
    }

}
