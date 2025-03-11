package com.cptkagan.ecommerce.DTOs.requestDTO;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "Name of the product", example = "Product 5", required = true)
    @NotBlank(message = "Name cannot be empty")
    @Size(min = 5, max = 50, message = "Name must be between 5 and 50 characters")
    private String name;

    @Schema(description = "Description of the product", example = "Headphone", required = true)
    @NotBlank(message = "Description cannot be empty")
    @Size(min = 10, max = 500, message = "Description must be between 10 and 500 characters")
    private String description;

    @Schema(description = "Price of the product", example = "1212", required = true)
    @NotNull(message = "Price cannot be empty")
    @Min(value = 1, message = "Price must be greater than 0")
    private Double price;

    @Schema(description = "Stock quantity of the product", example = "5", required = true)
    @NotNull(message = "Stock cannot be empty")
    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stockQuantity;

    @Schema(description = "Category of the product", example = "Electronics", required = true)
    @NotBlank(message = "Category cannot be empty")
    @Size(min = 3, max = 20, message = "Category must be between 3 and 20 characters")
    private String category;

    @Schema(description = "Low Stock warning of the product (default 5)", example = "5", required = false)
    private int lowStockWarning = 5;
}
