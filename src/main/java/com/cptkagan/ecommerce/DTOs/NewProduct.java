package com.cptkagan.ecommerce.DTOs;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NewProduct {

    @NotBlank(message = "Name cannot be empty")
    @Size(min = 5, max = 50, message = "Name must be between 5 and 50 characters")
    private String name;

    @NotBlank(message = "Description cannot be empty")
    @Size(min = 10, max = 500, message = "Description must be between 10 and 500 characters")
    private String description;

    @NotNull(message = "Price cannot be empty")
    @Min(value = 1, message = "Price must be greater than 0")
    private Double price;

    @NotNull(message = "Stock cannot be empty")
    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stockQuantity;

    @NotBlank(message = "Category cannot be empty")
    @Size(min = 3, max = 20, message = "Category must be between 3 and 20 characters")
    private String category;

}
