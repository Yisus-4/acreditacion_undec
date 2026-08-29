package com.undec.acreditacion.infrastructure.config;

import com.undec.acreditacion.application.input.LoginUseCase;
import com.undec.acreditacion.application.output.PasswordHasher;
import com.undec.acreditacion.application.output.UserRepository;
import com.undec.acreditacion.application.usecase.LoginService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Wires pure (framework-agnostic) application services to their input ports.
 *
 * <p>The application layer ({@link LoginService}) intentionally carries no
 * Spring annotations; this infrastructure configuration is the single place
 * that knows how to assemble and expose it as a Spring bean.</p>
 */
@Configuration
public class UseCaseConfiguration {

    @Bean
    public LoginUseCase loginUseCase(UserRepository userRepository, PasswordHasher passwordHasher) {
        return new LoginService(userRepository, passwordHasher);
    }
}
