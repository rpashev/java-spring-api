package com.rpashev.api.user.service;

import com.rpashev.api.auth.dto.AuthResponseDTO;
import com.rpashev.api.auth.exception.EmailAlreadyInUseException;
import com.rpashev.api.auth.exception.InvalidCredentialsException;
import com.rpashev.api.auth.exception.InvalidRefreshTokenException;
import com.rpashev.api.auth.security.JwtUtil;
import com.rpashev.api.user.dto.LoginUserDTO;
import com.rpashev.api.user.dto.RegisterUserDTO;
import com.rpashev.api.user.dto.UserDTO;
import com.rpashev.api.user.entity.User;
import com.rpashev.api.user.mapper.UserMapper;
import com.rpashev.api.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.userMapper = userMapper;
    }

    @Override
    public AuthResponseDTO register(RegisterUserDTO dto) {
        log.info("Registering user with email={}", dto.getEmail());
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyInUseException();
        }

        User user = userMapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        User saved = userRepository.save(user);

        String accessToken = jwtUtil.generateAccessToken(saved.getId().toString());
        String refreshToken = jwtUtil.generateRefreshToken(saved.getId().toString());

        return AuthResponseDTO.builder()
                .user(userMapper.toDto(saved))
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public AuthResponseDTO login(LoginUserDTO dto) {

        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }
        String accessToken = jwtUtil.generateAccessToken(user.getId().toString());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId().toString());

        return AuthResponseDTO.builder()
                .user(userMapper.toDto(user))
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

    }

    @Override
    public AuthResponseDTO refreshToken(String refreshToken) {
        if (!jwtUtil.validateRefreshToken(refreshToken)) {
            throw new InvalidRefreshTokenException();
        }

        String userId = jwtUtil.getSubject(refreshToken);
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(InvalidRefreshTokenException::new);

        String newAccessToken = jwtUtil.generateAccessToken(userId);
        String newRefreshToken = jwtUtil.generateRefreshToken(userId);

        return AuthResponseDTO.builder()
                .user(userMapper.toDto(user))
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    @Override
    public UserDTO getById(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

        return userMapper.toDto(user);
    }
}
