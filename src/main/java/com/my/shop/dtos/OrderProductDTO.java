package com.my.shop.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class OrderProductDTO {
    private Long productId;
    private int quantity;
}


