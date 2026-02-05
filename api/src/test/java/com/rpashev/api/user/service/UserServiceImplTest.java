package com.rpashev.api.user.service;

import com.rpashev.api.auth.dto.AuthResponseDTO;
import com.rpashev.api.auth.exception.InvalidCredentialsException;
import com.rpashev.api.auth.security.JwtUtil;
import com.rpashev.api.user.dto.LoginUserDTO;
import com.rpashev.api.user.dto.RegisterUserDTO;
import com.rpashev.api.user.entity.User;
import com.rpashev.api.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void registerThrowsWhenEmailExists() {
        RegisterUserDTO dto = new RegisterUserDTO();
        dto.setEmail("taken@example.com");
        dto.setPassword("password123");

        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> userService.register(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Email already in use");
    }

    @Test
    void registerCreatesUserAndReturnsToken() {
        RegisterUserDTO dto = new RegisterUserDTO();
        dto.setEmail("user@example.com");
        dto.setPassword("password123");
        dto.setFirstName("Test");
        dto.setLastName("User");

        UUID userId = UUID.randomUUID();
        User savedUser = User.builder()
                .id(userId)
                .email(dto.getEmail())
                .password("encoded")
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .build();

        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtUtil.generateToken(userId.toString())).thenReturn("jwt-token");

        AuthResponseDTO response = userService.register(dto);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getPassword()).isEqualTo("encoded");

        assertThat(response.getAccessToken()).isEqualTo("jwt-token");
        assertThat(response.getUser()).isNotNull();
        assertThat(response.getUser().getEmail()).isEqualTo(dto.getEmail());
    }

    @Test
    void loginThrowsWhenUserNotFound() {
        LoginUserDTO dto = new LoginUserDTO();
        dto.setEmail("missing@example.com");
        dto.setPassword("password123");

        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.login(dto))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void loginThrowsWhenPasswordMismatch() {
        LoginUserDTO dto = new LoginUserDTO();
        dto.setEmail("user@example.com");
        dto.setPassword("password123");

        User user = User.builder()
                .id(UUID.randomUUID())
                .email(dto.getEmail())
                .password("encoded")
                .build();

        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(dto.getPassword(), user.getPassword())).thenReturn(false);

        assertThatThrownBy(() -> userService.login(dto))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void loginReturnsTokenWhenCredentialsValid() {
        LoginUserDTO dto = new LoginUserDTO();
        dto.setEmail("user@example.com");
        dto.setPassword("password123");

        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .email(dto.getEmail())
                .password("encoded")
                .build();

        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(dto.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(userId.toString())).thenReturn("jwt-token");

        AuthResponseDTO response = userService.login(dto);

        assertThat(response.getAccessToken()).isEqualTo("jwt-token");
        assertThat(response.getUser()).isNotNull();
        assertThat(response.getUser().getEmail()).isEqualTo(dto.getEmail());
    }
}
