package com.cptkagan.ecommerce.DTOs.requestDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SellerRegisterRequest {
    @Schema(description = "Username of the seller", example = "john_doe", required = true)
    @Size(min = 5, max = 20, message = "Username must be between 5 and 20 characters")
    private String userName;

    @Schema(description = "Passowrd of the seller account", example = "securepassword", required = true)
    @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
    private String password;

    @Schema(description = "First name of the seller", example = "John", required = true)
    @Size(min = 3, max = 20, message = "First name must be between 3 and 20 characters")
    private String firstName;

    @Schema(description = "Last name of the seller", example = "Doe", required = true)
    @Size(min = 3, max = 20, message = "Last name must be between 3 and 20 characters")
    private String lastName;

    @Schema(description = "Email of the seller", example = "john_doe@example.com", required = true)
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Email must be valid")
    private String email;

    @Schema(description = "Phone number of the seller (10 digits)", example = "5551234567", required = true)
    @Size(min = 10, max = 10, message = "Phone number must be 10 characters")
    private String phoneNumber;

    // 1 for male, 0 for female, null for other or not specified
    @Schema(description = "Gender of the seller (true for male, false for female, null for unspecified)", example = "true", required = false)
    private Boolean gender;

    @Schema(description = "Company name of the seller", example = "Doe Products", required = true)
    @Size(min = 5, max = 20, message = "Company name must be between 5 and 20 characters")
    private String companyName;

    @Schema(description = "Address of the seller", example = "Example Address 1058/88", required = true)
    @Size(min = 10, max = 100, message = "Company address must be between 10 and 100 characters")
    private String address;

    @Schema(description = "Company description of the seller", example = "Best electrionics in the world!", required = true)
    @Size(min = 30, max = 500, message = "Company description must be between 30 and 500 characters")
    private String companyDescription;

    @Schema(description = "Tax number of the seller (10 digits)", example = "1234567890", required = true)
    @Size(min = 10, max = 10, message = "Tax number must be 10 characters")
    private String taxNumber;
}
