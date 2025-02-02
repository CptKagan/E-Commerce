package com.cptkagan.ecommerce.DTOs.requestDTO;

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
    @NotBlank(message = "Username cannot be empty")
    @Size(min = 5, max = 20, message = "Username must be between 5 and 20 characters")
    private String userName;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
    private String password;

    @NotBlank(message = "First name cannot be empty")
    @Size(min = 3, max = 20, message = "First name must be between 3 and 20 characters")
    private String firstName;

    @NotBlank(message = "Last name cannot be empty")
    @Size(min = 3, max = 20, message = "Last name must be between 3 and 20 characters")
    private String lastName;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Phone number cannot be empty")
    @Size(min = 10, max = 10, message = "Phone number must be 10 characters")
    private String phoneNumber;

    // 1 for male, 0 for female, null for other or not specified
    private Boolean gender;
}
