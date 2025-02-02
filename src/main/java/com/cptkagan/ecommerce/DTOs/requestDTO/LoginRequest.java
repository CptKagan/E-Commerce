package com.cptkagan.ecommerce.DTOs.requestDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginRequest {
    @NotBlank(message = "Username cannot be empty")
    @Size(min = 5, max = 20, message = "Username must be between 5 and 20 characters")
    private String userName;

    @NotBlank(message = "Password cannot be empty")
    private String password;
}
