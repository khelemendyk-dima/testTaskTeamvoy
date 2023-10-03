package com.my.shop.payloads.requests;

import com.my.shop.dtos.OrderProductDTO;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {
    private Long userId;
    private List<OrderProductDTO> products;
}
