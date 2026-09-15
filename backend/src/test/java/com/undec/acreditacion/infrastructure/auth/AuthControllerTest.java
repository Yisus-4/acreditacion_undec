package com.undec.acreditacion.infrastructure.auth;

import com.undec.acreditacion.application.exception.AuthenticationFailedException;
import com.undec.acreditacion.application.input.LoginUseCase;
import com.undec.acreditacion.application.output.LoginRateLimiter;
import com.undec.acreditacion.application.output.TokenService;
import com.undec.acreditacion.domain.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

final class AuthControllerTest {

    private MockMvc mockMvc;
    private LoginUseCase loginUseCase;
    private TokenService tokenService;
    private LoginRateLimiter rateLimiter;

    @BeforeEach
    void setUp() {
        loginUseCase = mock(LoginUseCase.class);
        tokenService = mock(TokenService.class);
        rateLimiter = mock(LoginRateLimiter.class);

        when(rateLimiter.tryConsume(anyString())).thenReturn(true);
        when(tokenService.generateToken(any(), any(), any())).thenReturn("mocked-jwt-token");

        mockMvc = MockMvcBuilders.standaloneSetup(new AuthController(loginUseCase, tokenService, rateLimiter))
                .setControllerAdvice(new AuthExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST /auth/login returns 200 with session payload on success")
    void returnsOkWithSessionResponseOnSuccess() throws Exception {
        User user = User.register("admin", "admin@undec.edu", "irrelevant-hash");
        when(loginUseCase.login(eq("admin@undec.edu"), eq("admin123"))).thenReturn(user);

        mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content("{\"email\":\"admin@undec.edu\",\"password\":\"admin123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.displayName").value("admin"))
                .andExpect(jsonPath("$.user.email").value("admin@undec.edu"))
                .andExpect(jsonPath("$.token").value("mocked-jwt-token"))
                .andExpect(jsonPath("$.issuedAt").isNumber());
    }

    @Test
    @DisplayName("POST /auth/login returns 429 when rate limit is exceeded")
    void returnsTooManyRequestsWhenRateLimited() throws Exception {
        when(rateLimiter.tryConsume(anyString())).thenReturn(false);

        mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content("{\"email\":\"admin@undec.edu\",\"password\":\"admin123\"}"))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.error").isString());
    }

    @Test
    @DisplayName("POST /auth/login returns 401 with a generic error on wrong password")
    void returnsUnauthorizedOnWrongPassword() throws Exception {
        when(loginUseCase.login(eq("admin@undec.edu"), eq("wrong")))
                .thenThrow(new AuthenticationFailedException());

        mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content("{\"email\":\"admin@undec.edu\",\"password\":\"wrong\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid credentials"));
    }

    @Test
    @DisplayName("POST /auth/login returns 401 with the same generic error for a nonexistent user")
    void returnsUnauthorizedForNonexistentUser() throws Exception {
        when(loginUseCase.login(eq("ghost@undec.edu"), eq("anything")))
                .thenThrow(new AuthenticationFailedException());

        mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content("{\"email\":\"ghost@undec.edu\",\"password\":\"anything\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid credentials"));
    }

    @Test
    @DisplayName("POST /auth/login returns 401 with the same generic error for an inactive user")
    void returnsUnauthorizedForInactiveUser() throws Exception {
        when(loginUseCase.login(eq("admin@undec.edu"), eq("admin123")))
                .thenThrow(new AuthenticationFailedException());

        mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content("{\"email\":\"admin@undec.edu\",\"password\":\"admin123\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid credentials"));
    }

    @Test
    @DisplayName("POST /auth/login returns 401 with a generic error for an invalid (blank) request")
    void returnsUnauthorizedForInvalidRequest() throws Exception {
        when(loginUseCase.login(eq("  "), eq("admin123")))
                .thenThrow(new AuthenticationFailedException());

        mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content("{\"email\":\"  \",\"password\":\"admin123\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid credentials"));
    }

    @Test
    @DisplayName("POST /auth/login returns 400 with a generic error for malformed JSON")
    void returnsBadRequestForMalformedJson() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content("{not-json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid request"));
    }
}
