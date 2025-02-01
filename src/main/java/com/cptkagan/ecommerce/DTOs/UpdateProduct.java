package com.cptkagan.ecommerce.DTOs;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateProduct {
    @Nullable // If it is null, it will not be checked
    @Size(min = 5, max = 50, message = "Name must be between 5 and 50 characters")
    private String name;

    @Nullable
    @Size(min = 10, max = 500, message = "Description must be between 10 and 500 characters")
    private String description;

    @Nullable
    @Min(value = 1, message = "Price must be greater than 0")
    private Double price;

    @Nullable
    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stockQuantity;

    @Nullable
    @Size(min = 3, max = 20, message = "Category must be between 3 and 20 characters")
    private String category;
}
