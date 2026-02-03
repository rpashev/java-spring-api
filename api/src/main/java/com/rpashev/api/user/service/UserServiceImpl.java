package com.rpashev.api.user.service;

import com.rpashev.api.auth.dto.AuthResponseDTO;
import com.rpashev.api.auth.exception.InvalidCredentialsException;
import com.rpashev.api.auth.security.JwtUtil;
import com.rpashev.api.user.dto.LoginUserDTO;
import com.rpashev.api.user.dto.RegisterUserDTO;
import com.rpashev.api.user.dto.UserDTO;
import com.rpashev.api.user.entity.User;
import com.rpashev.api.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.UUID;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public AuthResponseDTO register(RegisterUserDTO dto) {
        log.info("Registering user with email={}", dto.getEmail());
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already in use");
        }

        User user = User.builder()
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .image(dto.getImage())
                .build();

        User saved = userRepository.save(user);

        String token = jwtUtil.generateToken(saved.getId().toString());

        return AuthResponseDTO.builder()
                .user(mapToDTO(saved))
                .accessToken(token)
                .build();
    }

    @Override
    public AuthResponseDTO login(LoginUserDTO dto) {

        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }
        String token = jwtUtil.generateToken(user.getId().toString());

        return AuthResponseDTO.builder()
                .user(mapToDTO(user))
                .accessToken(token)
                .build();

    }

    @Override
    public UserDTO getById(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

        return mapToDTO(user);
    }

    private UserDTO mapToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .image(user.getImage())
                .build();
    }
}
