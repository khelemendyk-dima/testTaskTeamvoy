package com.my.shop.utils;

import com.my.shop.dtos.ProductDTO;
import com.my.shop.dtos.UserDTO;
import com.my.shop.models.Order;
import com.my.shop.models.OrderProduct;
import com.my.shop.models.Product;
import com.my.shop.models.User;
import com.my.shop.payloads.responses.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ConvertorUtil {
    private final ModelMapper modelMapper;

    public Product convertToProduct(com.my.shop.dtos.ProductDTO productDTO) {
        return modelMapper.map(productDTO, Product.class);
    }

    public ProductDTO convertToProductDTO(Product product) {
        return modelMapper.map(product, com.my.shop.dtos.ProductDTO.class);
    }

    public List<ProductDTO> convertListToProductDTO(List<Product> products) {
        List<com.my.shop.dtos.ProductDTO> productDTOS = new ArrayList<>();

        for (Product product : products) {
            productDTOS.add(convertToProductDTO(product));
        }

        return productDTOS;
    }

    public UserDTO convertToUserDTO(User user) {
        return modelMapper.map(user, UserDTO.class);
    }

    public OrderResponse convertToOrderResponse(Order order) {
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setOrderId(order.getId());
        orderResponse.setPaid(order.getPaid());
        orderResponse.setOrderTime(order.getOrderTime());
        orderResponse.setTotal(order.getTotal());

        List<ProductDTO> products = new ArrayList<>();

        for (OrderProduct orderProduct : order.getOrderProducts()) {
            Product product = orderProduct.getProduct();
            ProductDTO productDTO = new ProductDTO();
            productDTO.setId(product.getId());
            productDTO.setName(product.getName());
            productDTO.setPrice(product.getPrice());
            productDTO.setQuantity(orderProduct.getQuantity());

            products.add(productDTO);
        }

        orderResponse.setProducts(products);

        return orderResponse;
    }

    public List<OrderResponse> convertToOrderResponse(List<Order> orders) {
        List<OrderResponse> response = new ArrayList<>();

        for (Order order : orders) {
            response.add(convertToOrderResponse(order));
        }

        return response;
    }

}
