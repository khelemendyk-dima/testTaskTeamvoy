package com.my.shop.services.impl;

import com.my.shop.exceptions.NotFoundException;
import com.my.shop.exceptions.ProductAlreadyExistsException;
import com.my.shop.models.Product;
import com.my.shop.repositories.ProductRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
class ProductServiceImplTest {
    @InjectMocks
    private ProductServiceImpl productService;
    @Mock
    private ProductRepository productRepository;

    @Test
    void testSaveProduct() {
        Product expected = new Product(1L, "iPhone 12", 799.99, 10);

        when(productRepository.findByName(expected.getName())).thenReturn(Optional.empty());
        when(productRepository.save(expected)).thenReturn(expected);

        Product actual = productService.save(expected);

        assertEquals(expected, actual);
    }

    @Test
    void testSaveProductDuplicateName() {
        Product productToSave = new Product(1L, "iPhone 12", 799.99, 10);

        when(productRepository.findByName(productToSave.getName())).thenReturn(Optional.of(productToSave));

        assertThrows(ProductAlreadyExistsException.class, () -> productService.save(productToSave));
    }

    @Test
    void testGetAllProducts() {
        List<Product> expected = Arrays.asList(
                new Product(1L, "iPhone 12", 799.99, 10),
                new Product(1L, "iPhone 11", 699.99, 10)
        );

        when(productRepository.findAll()).thenReturn(expected);

        List<Product> actual = productService.getAll();

        assertEquals(expected, actual);
    }

    @Test
    void testGetAllProductsEmpty() {
        List<Product> expected = emptyList();

        when(productRepository.findAll()).thenReturn(expected);

        List<Product> actual = productService.getAll();

        assertEquals(expected, actual);
    }

    @Test
    void testFindProductById() {
        Long productId = 1L;
        Product expected = new Product(1L, "iPhone 12", 799.99, 10);

        when(productRepository.findById(productId)).thenReturn(Optional.of(expected));

        Product actual = productService.findById(productId);

        assertEquals(expected, actual);
    }

    @Test
    void testFindProductByIdNotFound() {
        Long productId = 1L;

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> productService.findById(productId));
    }
}

