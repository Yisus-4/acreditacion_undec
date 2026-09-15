package com.undec.acreditacion.infrastructure.config;

import com.undec.acreditacion.application.input.ChangeUserStatusUseCase;
import com.undec.acreditacion.application.input.CreateUserUseCase;
import com.undec.acreditacion.application.input.ListRolesUseCase;
import com.undec.acreditacion.application.input.ListUsersUseCase;
import com.undec.acreditacion.application.input.LoginUseCase;
import com.undec.acreditacion.application.input.RegisterUserUseCase;
import com.undec.acreditacion.application.input.UpdateUserUseCase;
import com.undec.acreditacion.application.output.PasswordHasher;
import com.undec.acreditacion.application.output.RoleRepository;
import com.undec.acreditacion.application.output.UserRepository;
import com.undec.acreditacion.application.usecase.ChangeUserStatusService;
import com.undec.acreditacion.application.usecase.CreateUserService;
import com.undec.acreditacion.application.usecase.ListRolesService;
import com.undec.acreditacion.application.usecase.ListUsersService;
import com.undec.acreditacion.application.usecase.LoginService;
import com.undec.acreditacion.application.usecase.RegisterUserService;
import com.undec.acreditacion.application.usecase.UpdateUserService;
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

    @Bean
    public RegisterUserUseCase registerUserUseCase(UserRepository userRepository) {
        return new RegisterUserService(userRepository);
    }

    @Bean
    public ListUsersUseCase listUsersUseCase(UserRepository userRepository) {
        return new ListUsersService(userRepository);
    }

    @Bean
    public ListRolesUseCase listRolesUseCase(RoleRepository roleRepository) {
        return new ListRolesService(roleRepository);
    }

    @Bean
    public ChangeUserStatusUseCase changeUserStatusUseCase(UserRepository userRepository) {
        return new ChangeUserStatusService(userRepository);
    }

    @Bean
    public UpdateUserUseCase updateUserUseCase(UserRepository userRepository,
                                             RoleRepository roleRepository,
                                             PasswordHasher passwordHasher) {
        return new UpdateUserService(userRepository, roleRepository, passwordHasher);
    }

    @Bean
    public CreateUserUseCase createUserUseCase(UserRepository userRepository,
                                             RoleRepository roleRepository,
                                             PasswordHasher passwordHasher) {
        return new CreateUserService(userRepository, roleRepository, passwordHasher);
    }
}
