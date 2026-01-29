package com.rpashev.api.user.service;

import com.rpashev.api.auth.dto.AuthResponseDTO;
import com.rpashev.api.user.dto.LoginUserDTO;
import com.rpashev.api.user.dto.RegisterUserDTO;
import com.rpashev.api.user.dto.UserDTO;

import java.util.UUID;

public interface UserService {
    AuthResponseDTO register(RegisterUserDTO dto);

    AuthResponseDTO login(LoginUserDTO dto);

    UserDTO getById(UUID id);

}
