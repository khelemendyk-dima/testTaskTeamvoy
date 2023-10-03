package com.my.shop.services.impl;

import com.my.shop.dtos.OrderProductDTO;
import com.my.shop.exceptions.NotEnoughProductException;
import com.my.shop.exceptions.NotFoundException;
import com.my.shop.models.Order;
import com.my.shop.models.OrderProduct;
import com.my.shop.models.Product;
import com.my.shop.models.User;
import com.my.shop.repositories.OrderRepository;
import com.my.shop.services.OrderService;
import com.my.shop.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final ProductService productService;

    @Transactional
    @Override
    public Order save(User user, List<OrderProductDTO> products) {
        Order order = new Order();
        order.setUser(user);
        order.setPaid(false);
        order.setOrderTime(LocalDateTime.now());

        double total = 0.0;
        for (OrderProductDTO productDTO : products) {
            Product product = productService.findById(productDTO.getProductId());
            updateQuantityOfProduct(productDTO, product);
            OrderProduct orderProduct = createOrderProduct(product, order, productDTO.getQuantity());
            total += product.getPrice() * productDTO.getQuantity();
            order.getOrderProducts().add(orderProduct);
        }
        order.setTotal(total);

        order = orderRepository.save(order);

        // set order with id to orderProducts
        List<OrderProduct> orderProducts = order.getOrderProducts();
        for (OrderProduct orderProduct : orderProducts) {
            orderProduct.setOrder(order);
        }

        return order;
    }

    @Override
    public List<Order> getAllOrdersByUserId(Long userId) {
        return orderRepository.findAllByUserId(userId);
    }

    @Override
    public Order findById(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(() ->
                new NotFoundException(String.format("Order with id '%d' not found", orderId)));
    }

    @Transactional
    @Override
    public void payOrder(Long orderId) {
        Order order = findById(orderId);
        order.setPaid(true);
    }

    @Transactional
    @Override
    public void deleteUnpaidOrders() {
        LocalDateTime tenMinutesAgo = LocalDateTime.now().minusMinutes(10);
        List<Order> unpaidOrders = orderRepository.findByPaidFalseAndOrderTimeBefore(tenMinutesAgo);

        for (Order order : unpaidOrders) {
            List<OrderProduct> orderProducts = order.getOrderProducts();

            for (OrderProduct orderProduct : orderProducts) {
                Product product = orderProduct.getProduct();
                product.setQuantity(product.getQuantity() + orderProduct.getQuantity());
            }
        }

        orderRepository.deleteAll(unpaidOrders);
    }

    private void updateQuantityOfProduct(OrderProductDTO productDTO, Product product) {
        int remainder = product.getQuantity() - productDTO.getQuantity();

        if (remainder < 0) {
            throw new NotEnoughProductException(
                    String.format("We don't have product '%s' in quantity '%d'",
                            product.getName(), productDTO.getQuantity()));
        }

        product.setQuantity(remainder);
    }

    private OrderProduct createOrderProduct(Product product, Order order, int orderProductQuantity) {
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setProduct(product);
        orderProduct.setOrder(order);
        orderProduct.setQuantity(orderProductQuantity);

        return orderProduct;
    }
}
