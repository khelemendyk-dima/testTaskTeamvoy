package com.my.shop.utils;

import com.my.shop.dtos.ProductDTO;
import com.my.shop.dtos.UserDTO;
import com.my.shop.models.Product;
import com.my.shop.models.User;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ConvertorUtil {
    private final ModelMapper modelMapper;

    public Product convertToProduct(ProductDTO productDTO) {
        return modelMapper.map(productDTO, Product.class);
    }

    public ProductDTO convertToProductDTO(Product product) {
        return modelMapper.map(product, ProductDTO.class);
    }

    public List<ProductDTO> convertListToProductDTO(List<Product> products) {
        List<ProductDTO> productDTOS = new ArrayList<>();

        for (Product product : products) {
            productDTOS.add(convertToProductDTO(product));
        }

        return productDTOS;
    }

    public UserDTO convertToUserDTO(User user) {
        return modelMapper.map(user, UserDTO.class);
    }
}
