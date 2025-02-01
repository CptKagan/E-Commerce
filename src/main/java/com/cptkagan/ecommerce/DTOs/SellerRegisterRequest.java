package com.cptkagan.ecommerce.DTOs;

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
    @Size(min = 5, max = 20, message = "Username must be between 5 and 20 characters")
    private String userName;

    @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
    private String password;

    @Size(min = 3, max = 20, message = "First name must be between 3 and 20 characters")
    private String firstName;

    @Size(min = 3, max = 20, message = "Last name must be between 3 and 20 characters")
    private String lastName;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Email must be valid")
    private String email;

    @Size(min = 10, max = 10, message = "Phone number must be 10 characters")
    private String phoneNumber;

    // 1 for male, 0 for female, null for other or not specified
    private Boolean gender;

    @Size(min = 5, max = 20, message = "Company name must be between 5 and 20 characters")
    private String companyName;

    @Size(min = 10, max = 100, message = "Company address must be between 10 and 100 characters")
    private String address;

    @Size(min = 30, max = 500, message = "Company description must be between 30 and 500 characters")
    private String companyDescription;

    @Size(min = 10, max = 10, message = "Tax number must be 10 characters")
    private String taxNumber;
}
