package com.my.shop.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.shop.dtos.ProductDTO;
import com.my.shop.exceptions.ProductAlreadyExistsException;
import com.my.shop.models.Product;
import com.my.shop.services.impl.ProductServiceImpl;
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

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ConvertorUtil convertor;

    @MockBean
    private ProductServiceImpl productService;

    @Test
    @WithMockUser(username = "manager", roles = "MANAGER")
    void testSaveProductAsManager() throws Exception {
        String productDTOJson = "{\"name\":\"Iphone 12\"," +
                                 "\"price\":\"944.99\"," +
                                 "\"quantity\":\"13\"}";

        Product productFromDB = new Product(1L, "Iphone 12", 944.99, 13);
        ProductDTO productDTO = convertor.convertToProductDTO(productFromDB);
        String expectedJson = objectMapper.writeValueAsString(productDTO);

        when(productService.save(any())).thenReturn(productFromDB);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productDTOJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(expectedJson));
    }

    @Test
    @WithMockUser(username = "manager", roles = "MANAGER")
    void testSaveProductAlreadyExistsAsManager() throws Exception {
        String productDTOJson = "{\"name\":\"Iphone 12\"," +
                                 "\"price\":\"944.99\"," +
                                 "\"quantity\":\"13\"}";

        when(productService.save(any())).thenThrow(ProductAlreadyExistsException.class);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productDTOJson))
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    @WithMockUser(username = "client", roles = "CLIENT")
    void testGetAllProductsAsClient() throws Exception {
        List<Product> products = new ArrayList<>();
        products.add(new Product(1L, "Iphone 12", 944.99, 13));
        products.add(new Product(2L, "Iphone 13", 1100.99, 7));

        List<ProductDTO> productDTOS = convertor.convertListToProductDTO(products);
        String expectedJson = objectMapper.writeValueAsString(productDTOS);

        when(productService.getAll()).thenReturn(products);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/products"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(expectedJson));
    }

}


