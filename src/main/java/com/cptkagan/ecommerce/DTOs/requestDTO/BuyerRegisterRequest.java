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
public class BuyerRegisterRequest {
    @Schema(description = "Username of the buyer", example = "john_doe", required = true)
    @NotBlank(message = "Username cannot be empty")
    @Size(min = 5, max = 20, message = "Username must be between 5 and 20 characters")
    private String userName;

    @Schema(description = "Password of the buyer account", example = "securepassword", required = true)
    @NotBlank(message = "Password cannot be empty")
    @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
    private String password;

    @Schema(description = "First name of the buyer", example = "John", required = true)
    @NotBlank(message = "First name cannot be empty")
    @Size(min = 3, max = 20, message = "First name must be between 3 and 20 characters")
    private String firstName;

    @Schema(description = "Last name of the buyer", example = "Doe", required = true)
    @NotBlank(message = "Last name cannot be empty")
    @Size(min = 3, max = 20, message = "Last name must be between 3 and 20 characters")
    private String lastName;

    @Schema(description = "Email address of the buyer", example = "john.doe@example.com", required = true)
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Email must be valid")
    private String email;

    @Schema(description = "Phone number of the buyer (10 digits)", example = "5551234567", required = true)
    @NotBlank(message = "Phone number cannot be empty")
    @Size(min = 10, max = 10, message = "Phone number must be 10 characters")
    private String phoneNumber;

    @Schema(description = "Gender of the buyer (true for male, false for female, null for unspecified)", example = "true", required = false)
    private Boolean gender;
}
