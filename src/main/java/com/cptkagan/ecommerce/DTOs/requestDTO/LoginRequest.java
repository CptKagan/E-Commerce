package com.cptkagan.ecommerce.DTOs.requestDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginRequest {
    @Schema(description = "Username of the buyer account", example = "john_doe", required = true)
    @NotBlank(message = "Username cannot be empty")
    @Size(min = 5, max = 20, message = "Username must be between 5 and 20 characters")
    private String userName;

    @Schema(description = "Password of the buyer account", example = "securepassword", required = true)
    @NotBlank(message = "Password cannot be empty")
    private String password;
}
