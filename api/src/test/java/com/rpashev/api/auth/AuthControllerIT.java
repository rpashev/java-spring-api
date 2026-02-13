package com.rpashev.api.auth;

import com.rpashev.api.auth.dto.AuthResponseDTO;
import com.rpashev.api.auth.dto.RefreshTokenRequestDTO;
import com.rpashev.api.support.IntegrationTestBase;
import com.rpashev.api.user.dto.LoginUserDTO;
import com.rpashev.api.user.dto.RegisterUserDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AuthControllerIT extends IntegrationTestBase {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void registerCreatesUserAndReturnsToken() {
        RegisterUserDTO dto = new RegisterUserDTO();
        dto.setEmail(uniqueEmail());
        dto.setPassword("password123");
        dto.setFirstName("Test");
        dto.setLastName("User");

        ResponseEntity<AuthResponseDTO> response = restTemplate.postForEntity(
                "/auth/register",
                dto,
                AuthResponseDTO.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getAccessToken()).isNotBlank();
        assertThat(response.getBody().getRefreshToken()).isNotBlank();
        assertThat(response.getBody().getUser()).isNotNull();
        assertThat(response.getBody().getUser().getEmail()).isEqualTo(dto.getEmail());
    }

    @Test
    void loginReturnsTokenAfterRegistration() {
        RegisterUserDTO register = new RegisterUserDTO();
        register.setEmail(uniqueEmail());
        register.setPassword("password123");
        register.setFirstName("Login");
        register.setLastName("User");

        restTemplate.postForEntity("/auth/register", register, AuthResponseDTO.class);

        LoginUserDTO login = new LoginUserDTO();
        login.setEmail(register.getEmail());
        login.setPassword(register.getPassword());

        ResponseEntity<AuthResponseDTO> response = restTemplate.postForEntity(
                "/auth/login",
                login,
                AuthResponseDTO.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getAccessToken()).isNotBlank();
        assertThat(response.getBody().getRefreshToken()).isNotBlank();
        assertThat(response.getBody().getUser()).isNotNull();
    }

    @Test
    void registerReturnsConflictWhenEmailAlreadyExists() {
        String email = uniqueEmail();

        RegisterUserDTO first = new RegisterUserDTO();
        first.setEmail(email);
        first.setPassword("password123");
        first.setFirstName("First");
        first.setLastName("User");
        restTemplate.postForEntity("/auth/register", first, AuthResponseDTO.class);

        RegisterUserDTO duplicate = new RegisterUserDTO();
        duplicate.setEmail(email);
        duplicate.setPassword("password123");
        duplicate.setFirstName("Duplicate");
        duplicate.setLastName("User");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/auth/register",
                duplicate,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).contains("Email already in use");
    }

    @Test
    void refreshReturnsNewTokensWhenRefreshTokenValid() {
        AuthResponseDTO auth = registerAndLogin();

        RefreshTokenRequestDTO request = new RefreshTokenRequestDTO();
        request.setRefreshToken(auth.getRefreshToken());

        ResponseEntity<AuthResponseDTO> response = restTemplate.postForEntity(
                "/auth/refresh",
                request,
                AuthResponseDTO.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getAccessToken()).isNotBlank();
        assertThat(response.getBody().getRefreshToken()).isNotBlank();
        assertThat(response.getBody().getUser()).isNotNull();
    }

    @Test
    void refreshReturnsUnauthorizedWhenRefreshTokenInvalid() {
        RefreshTokenRequestDTO request = new RefreshTokenRequestDTO();
        request.setRefreshToken("invalid-refresh-token");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/auth/refresh",
                request,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void protectedHealthRequiresAuthentication() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/health/protected",
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void protectedHealthAllowsAuthenticatedUser() {
        AuthResponseDTO auth = registerAndLogin();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(auth.getAccessToken());

        ResponseEntity<String> response = restTemplate.exchange(
                "/health/protected",
                org.springframework.http.HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("You are authenticated!");
    }

    private AuthResponseDTO registerAndLogin() {
        RegisterUserDTO register = new RegisterUserDTO();
        register.setEmail(uniqueEmail());
        register.setPassword("password123");
        register.setFirstName("Protected");
        register.setLastName("User");

        ResponseEntity<AuthResponseDTO> registerResponse = restTemplate.postForEntity(
                "/auth/register",
                register,
                AuthResponseDTO.class
        );

        assertThat(registerResponse.getBody()).isNotNull();
        return registerResponse.getBody();
    }

    private String uniqueEmail() {
        return "user-" + UUID.randomUUID() + "@example.com";
    }
}
