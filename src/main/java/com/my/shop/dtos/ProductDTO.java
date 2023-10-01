package com.my.shop.dtos;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class ProductDTO {
    private Long id;

    @Size(min = 1, max = 45, message = "Product name must be between 1 and 45 characters")
    private String name;

    @NotNull(message = "Price must not be null")
    @DecimalMin(value = "0.0", message = "Price must be positive")
    private Double price;

    @NotNull(message = "Quantity must not be null")
    @Min(value = 1, message = "You take one item at least")
    private Integer quantity;
}
