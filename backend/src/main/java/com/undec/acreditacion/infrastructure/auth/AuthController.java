package com.undec.acreditacion.infrastructure.auth;

import com.undec.acreditacion.application.input.LoginUseCase;
import com.undec.acreditacion.domain.entities.User;
import com.undec.acreditacion.infrastructure.auth.dto.LoginRequest;
import com.undec.acreditacion.infrastructure.auth.dto.LoginResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * HTTP adapter for authentication. Translates request/response only; all
 * credential logic is delegated to {@link LoginUseCase}.
 *
 * <p>The response is a development-only demo contract (see {@link LoginResponse}):
 * it never presents a {@code token} as if authentication were complete.</p>
 */
@RestController
@RequestMapping("/auth")
public final class AuthController {

    private final LoginUseCase loginUseCase;

    public AuthController(LoginUseCase loginUseCase) {
        this.loginUseCase = loginUseCase;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        User user = loginUseCase.login(request.email(), request.password());
        LoginResponse response = LoginResponse.demo(
                new LoginResponse.UserView(user.getId(), user.getUsername(), user.getEmail(), user.isActive()));
        return ResponseEntity.ok(response);
    }
}
