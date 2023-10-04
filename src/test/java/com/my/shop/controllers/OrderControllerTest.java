package com.my.shop.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.shop.exceptions.NotFoundException;
import com.my.shop.models.*;
import com.my.shop.payloads.responses.OrderResponse;
import com.my.shop.services.impl.OrderServiceImpl;
import com.my.shop.services.impl.UserServiceImpl;
import com.my.shop.utils.ConvertorUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ConvertorUtil convertor;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderServiceImpl orderService;
    @MockBean
    private UserServiceImpl userService;

    @Test
    @WithMockUser(username = "client", roles = "CLIENT")
    void testCreateOrderAsClient() throws Exception {
        String orderRequest = "{\"userId\":\"1\"," +
                               "\"products\":[{" +
                               "\"productId\":\"1\"," +
                               "\"quantity\":\"5\"},{" +
                               "\"productId\":\"3\"," +
                               "\"quantity\":\"1\"}]}";

        User user = new User(1L, "client", "secretWord", Role.ROLE_CLIENT);
        Order order = getTestOrder(user);
        OrderResponse orderResponse = convertor.convertToOrderResponse(order);
        String expectedJson = objectMapper.writeValueAsString(orderResponse);

        when(userService.findById(user.getId())).thenReturn(user);
        when(orderService.save(any(), any())).thenReturn(order);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(orderRequest))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(expectedJson));
    }

    @Test
    @WithMockUser(username = "client", roles = "CLIENT")
    void testCreateOrderAsClientUserNotFound() throws Exception {
        String orderRequest = "{\"userId\":\"1\"," +
                "\"products\":[{" +
                "\"productId\":\"1\"," +
                "\"quantity\":\"5\"},{" +
                "\"productId\":\"3\"," +
                "\"quantity\":\"1\"}]}";

        when(userService.findById(1L)).thenThrow(NotFoundException.class);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(orderRequest))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @WithMockUser(username = "client", roles = "CLIENT")
    void testCreateOrderAsClientProductNotFound() throws Exception {
        String orderRequest = "{\"userId\":\"1\"," +
                "\"products\":[{" +
                "\"productId\":\"1\"," +
                "\"quantity\":\"5\"},{" +
                "\"productId\":\"3\"," +
                "\"quantity\":\"1\"}]}";
        User user = new User(1L, "client", "secretWord", Role.ROLE_CLIENT);

        when(userService.findById(user.getId())).thenReturn(user);
        when(orderService.save(any(), any())).thenThrow(NotFoundException.class);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(orderRequest))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @WithMockUser(username = "client", roles = "CLIENT")
    void testAllUserOrders() throws Exception {
        User user = new User(1L, "client", "secretWord", Role.ROLE_CLIENT);
        List<Order> orders = Collections.singletonList(getTestOrder(user));
        String expectedJson = objectMapper.writeValueAsString(convertor.convertToOrderResponse(orders));

        when(userService.findByUsername(user.getUsername())).thenReturn(user);
        when(orderService.getAllOrdersByUserId(user.getId())).thenReturn(orders);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/orders/my-orders"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(expectedJson));
    }

    @Test
    @WithMockUser(username = "client", roles = "CLIENT")
    void testPayOrder() throws Exception {
        User user = new User(1L, "client", "secretWord", Role.ROLE_CLIENT);
        Order order = getTestOrder(user);

        when(userService.findByUsername(user.getUsername())).thenReturn(user);
        when(orderService.findById(order.getId())).thenReturn(order);

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/orders/pay/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Order was successfully paid"));
    }

    @Test
    @WithMockUser(username = "client", roles = "CLIENT")
    void testPayOrderNotFound() throws Exception {
        User user = new User(1L, "client", "secretWord", Role.ROLE_CLIENT);
        Order order = getTestOrder(user);

        User loggedUser = new User(2L, "loggedUser", "secretWord", Role.ROLE_CLIENT);

        when(userService.findByUsername(user.getUsername())).thenReturn(loggedUser);
        when(orderService.findById(order.getId())).thenReturn(order);

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/orders/pay/1"))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().string("You can pay only your orders"));
    }

    private Order getTestOrder(User user) {
        Order order = new Order();
        order.setId(1L);
        order.setUser(user);
        order.setPaid(false);
        order.setOrderTime(LocalDateTime.now());
        order.setTotal(1111.11);

        Product product1 = new Product(1L, "Iphone 12", 111.11, 15);
        Product product2 = new Product(3L, "Iphone 13", 111.11, 7);

        OrderProduct orderProduct1 = new OrderProduct(1L, order, product1, 5);
        OrderProduct orderProduct2 = new OrderProduct(2L, order, product2, 1);

        order.setOrderProducts(List.of(orderProduct1, orderProduct2));

        return order;
    }
}
