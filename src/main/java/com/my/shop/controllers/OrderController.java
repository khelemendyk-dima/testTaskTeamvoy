package com.my.shop.controllers;

import com.my.shop.models.Order;
import com.my.shop.models.User;
import com.my.shop.payloads.requests.OrderRequest;
import com.my.shop.payloads.responses.OrderResponse;
import com.my.shop.services.OrderService;
import com.my.shop.services.UserService;
import com.my.shop.utils.ConvertorUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/orders")
public class OrderController {
    private final OrderService orderService;
    private final UserService userService;
    private final ConvertorUtil convertor;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest request) {
        User user = userService.findById(request.getUserId());

        Order order = orderService.save(user, request.getProducts());

        OrderResponse orderResponse = convertor.convertToOrderResponse(order);

        return ResponseEntity.ok(orderResponse);
    }

    @GetMapping("my-orders")
    public ResponseEntity<List<OrderResponse>> getAllUserOrders() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User loggedUser = userService.findByUsername(auth.getName());

        List<Order> orders = orderService.getAllOrdersByUserId(loggedUser.getId());

        List<OrderResponse> response = convertor.convertToOrderResponse(orders);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("pay/{orderId}")
    public ResponseEntity<String> payOrder(@PathVariable Long orderId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User loggedUser = userService.findByUsername(auth.getName());

        Order order = orderService.findById(orderId);

        if (loggedUser.getId().equals(order.getUser().getId())) {
            orderService.payOrder(orderId);
        } else {
            return new ResponseEntity<>("You can pay only your orders", HttpStatus.FORBIDDEN);
        }

        return ResponseEntity.ok("Order was successfully paid");
    }
}
