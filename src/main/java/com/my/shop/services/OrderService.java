package com.my.shop.services;

import com.my.shop.dtos.OrderProductDTO;
import com.my.shop.models.Order;
import com.my.shop.models.User;

import java.util.List;

public interface OrderService {
    Order save(User user, List<OrderProductDTO> products);
    List<Order> getAllOrdersByUserId(Long userId);
    Order findById(Long orderId);
    void payOrder(Long orderId);
    void deleteUnpaidOrders();
}
