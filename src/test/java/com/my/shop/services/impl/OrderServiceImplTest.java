package com.my.shop.services.impl;

import com.my.shop.dtos.OrderProductDTO;
import com.my.shop.exceptions.NotEnoughProductException;
import com.my.shop.exceptions.NotFoundException;
import com.my.shop.models.*;
import com.my.shop.repositories.OrderRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class OrderServiceImplTest {
    @InjectMocks
    private OrderServiceImpl orderService;
    @Mock
    private ProductServiceImpl productService;
    @Mock
    private OrderRepository orderRepository;

    @Test
    void testSaveOrder() {
        User user = new User(1L, "test user", "secretPass", Role.ROLE_CLIENT);
        Order expected = getTestOrder(user);
        List<OrderProductDTO> products = getTestOrderProductDTO();

        Product productFromDB1 = new Product(1L, "Iphone 12", 111.11, 15);
        Product productFromDB2 = new Product(3L, "Iphone 13", 111.11, 7);

        when(productService.findById(1L)).thenReturn(productFromDB1);
        when(productService.findById(3L)).thenReturn(productFromDB2);
        when(orderRepository.save(any())).thenReturn(expected);

        Order actual = orderService.save(user, products);

        assertEquals(expected, actual);
    }

    @Test
    void testSaveOrderNotFoundProduct() {
        User user = new User(1L, "test user", "secretPass", Role.ROLE_CLIENT);
        List<OrderProductDTO> products = getTestOrderProductDTO();

        when(productService.findById(1L)).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> orderService.save(user, products));
    }

    @Test
    void testSaveOrderNotEnoughQuantityOfProduct() {
        User user = new User(1L, "test user", "secretPass", Role.ROLE_CLIENT);
        List<OrderProductDTO> products = getTestOrderProductDTO();

        Product productFromDB1 = new Product(1L, "Iphone 12", 111.11, 1);

        // our order's product want to order product quantity that greater that we have in db
        when(productService.findById(1L)).thenReturn(productFromDB1);

        assertThrows(NotEnoughProductException.class, () -> orderService.save(user, products));
    }

    @Test
    void testGetAllOrdersByUserId() {
        User user = new User(1L, "test user", "secretPass", Role.ROLE_CLIENT);
        List<Order> expected = Collections.singletonList(getTestOrder(user));

        when(orderRepository.findAllByUserId(user.getId())).thenReturn(expected);

        List<Order> actual = orderService.getAllOrdersByUserId(user.getId());

        assertEquals(expected, actual);
    }

    @Test
    void testFindOrderById() {
        Long orderId = 1L;
        User user = new User(1L, "test user", "secretPass", Role.ROLE_CLIENT);
        Order expected = getTestOrder(user);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(expected));

        Order actual = orderService.findById(orderId);

        assertEquals(expected, actual);
    }

    @Test
    void testFindOrderByIdNotFound() {
        Long orderId = 1L;

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.findById(orderId));
    }

    @Test
    void testPayOrder() {
        Long orderId = 1L;
        User user = new User(1L, "test user", "secretPass", Role.ROLE_CLIENT);
        Order order = getTestOrder(user);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        assertDoesNotThrow(() -> orderService.payOrder(orderId));
    }

    @Test
    void testPayOrderNotFound() {
        Long orderId = 1L;

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.payOrder(orderId));
    }

    @Test
    void testDeleteUnpaidOrders() {
        LocalDateTime tenMinutesAgo = LocalDateTime.now().minusMinutes(10);

        List<Order> unpaidOrders = new ArrayList<>();

        when(orderRepository.findByPaidFalseAndOrderTimeBefore(tenMinutesAgo)).thenReturn(unpaidOrders);

        orderService.deleteUnpaidOrders();

        verify(orderRepository).deleteAll(unpaidOrders);
    }


    private List<OrderProductDTO> getTestOrderProductDTO() {
        List<OrderProductDTO> products = new ArrayList<>();

        products.add(new OrderProductDTO(1L, 5));
        products.add(new OrderProductDTO(3L, 2));

        return products;
    }

    private Order getTestOrder(User user) {
        Order order = new Order();
        order.setId(1L);
        order.setUser(user);
        order.setPaid(false);
        order.setOrderTime(LocalDateTime.now());
        order.setTotal(777.77);

        Product product1 = new Product(1L, "Iphone 12", 111.11, 15);
        Product product2 = new Product(3L, "Iphone 13", 111.11, 7);

        OrderProduct orderProduct1 = new OrderProduct(1L, order, product1, 5);
        OrderProduct orderProduct2 = new OrderProduct(2L, order, product2, 2);

        order.setOrderProducts(List.of(orderProduct1, orderProduct2));

        return order;
    }
}
