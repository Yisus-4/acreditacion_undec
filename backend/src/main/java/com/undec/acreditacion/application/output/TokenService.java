package com.undec.acreditacion.application.output;

import com.undec.acreditacion.domain.auth.TokenPayload;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TokenService {

    String generateToken(UUID userId, String email, List<String> roles);

    Optional<TokenPayload> validateToken(String token);
}
