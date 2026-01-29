package com.rpashev.api.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginUserDTO {

    @Email
    @NotBlank
    @Size(max = 255)
    private String email;

    @Size(max = 255)
    @NotBlank
    private String password;
}
