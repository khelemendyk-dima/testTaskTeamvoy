package com.my.shop.payloads.responses;

import com.my.shop.dtos.ProductDTO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponse {
    private Long orderId;
    private Boolean paid;
    private LocalDateTime orderTime;
    private Double total;
    private List<ProductDTO> products;
}
