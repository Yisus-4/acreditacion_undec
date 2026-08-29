package com.undec.acreditacion.application.usecase;

import com.undec.acreditacion.application.exception.AuthenticationFailedException;
import com.undec.acreditacion.application.output.PasswordHasher;
import com.undec.acreditacion.application.output.UserRepository;
import com.undec.acreditacion.domain.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pure unit tests for {@link LoginService}. No Spring context, no real BCrypt:
 * a fake hasher keeps the use case isolated and deterministic.
 */
final class LoginServiceTest {

    private FakeUserRepository userRepository;
    private FakePasswordHasher passwordHasher;
    private LoginService loginService;

    @BeforeEach
    void setUp() {
        userRepository = new FakeUserRepository();
        passwordHasher = new FakePasswordHasher();
        loginService = new LoginService(userRepository, passwordHasher);
    }

    @Test
    @DisplayName("succeeds when the email exists and the password matches")
    void loginSucceedsWhenPasswordMatches() {
        User user = User.register("admin", "admin@undec.edu", passwordHasher.hash("admin123"));
        userRepository.save(user);

        User authenticated = loginService.login("admin@undec.edu", "admin123");

        assertEquals(user.getId(), authenticated.getId());
        assertTrue(authenticated.isActive());
    }

    @Test
    @DisplayName("fails when the password is incorrect")
    void failsWhenPasswordIsIncorrect() {
        User user = User.register("admin", "admin@undec.edu", passwordHasher.hash("admin123"));
        userRepository.save(user);

        assertThrows(AuthenticationFailedException.class,
                () -> loginService.login("admin@undec.edu", "wrong-password"));
    }

    @Test
    @DisplayName("fails with the same generic error when the user does not exist")
    void failsWithGenericErrorWhenUserDoesNotExist() {
        AuthenticationFailedException thrown = assertThrows(AuthenticationFailedException.class,
                () -> loginService.login("ghost@undec.edu", "anything"));

        assertEquals(AuthenticationFailedException.GENERIC_MESSAGE, thrown.getMessage());
    }

    @Test
    @DisplayName("fails when the user is inactive")
    void failsWhenUserIsInactive() {
        User user = User.register("admin", "admin@undec.edu", passwordHasher.hash("admin123"));
        user.deactivate();
        userRepository.save(user);

        assertThrows(AuthenticationFailedException.class,
                () -> loginService.login("admin@undec.edu", "admin123"));
    }

    @Test
    @DisplayName("does not distinguish not-found from wrong password: same message")
    void notFoundAndWrongPasswordShareTheSameGenericMessage() {
        User user = User.register("admin", "admin@undec.edu", passwordHasher.hash("admin123"));
        userRepository.save(user);

        AuthenticationFailedException notFound = assertThrows(AuthenticationFailedException.class,
                () -> loginService.login("ghost@undec.edu", "anything"));
        AuthenticationFailedException wrongPassword = assertThrows(AuthenticationFailedException.class,
                () -> loginService.login("admin@undec.edu", "wrong-password"));

        assertEquals(notFound.getMessage(), wrongPassword.getMessage());
    }

    @Test
    @DisplayName("fails with the generic error when the email is blank")
    void failsWithGenericErrorWhenEmailIsBlank() {
        AuthenticationFailedException thrown = assertThrows(AuthenticationFailedException.class,
                () -> loginService.login("  ", "admin123"));

        assertEquals(AuthenticationFailedException.GENERIC_MESSAGE, thrown.getMessage());
    }

    @Test
    @DisplayName("fails with the generic error when the email is null")
    void failsWithGenericErrorWhenEmailIsNull() {
        assertThrows(AuthenticationFailedException.class,
                () -> loginService.login(null, "admin123"));
    }

    @Test
    @DisplayName("fails with the generic error when the password is blank")
    void failsWithGenericErrorWhenPasswordIsBlank() {
        AuthenticationFailedException thrown = assertThrows(AuthenticationFailedException.class,
                () -> loginService.login("admin@undec.edu", "   "));

        assertEquals(AuthenticationFailedException.GENERIC_MESSAGE, thrown.getMessage());
    }

    @Test
    @DisplayName("fails with the generic error when the password is null")
    void failsWithGenericErrorWhenPasswordIsNull() {
        assertThrows(AuthenticationFailedException.class,
                () -> loginService.login("admin@undec.edu", null));
    }

    @Test
    @DisplayName("invalid input shares the same generic message as authentication failures")
    void invalidInputSharesTheSameGenericMessageAsAuthFailures() {
        User user = User.register("admin", "admin@undec.edu", passwordHasher.hash("admin123"));
        userRepository.save(user);

        AuthenticationFailedException invalidInput = assertThrows(AuthenticationFailedException.class,
                () -> loginService.login("admin@undec.edu", "  "));
        AuthenticationFailedException wrongPassword = assertThrows(AuthenticationFailedException.class,
                () -> loginService.login("admin@undec.edu", "wrong"));

        assertEquals(invalidInput.getMessage(), wrongPassword.getMessage());
    }

    /** Minimal in-memory UserRepository for the use case tests. */
    private static final class FakeUserRepository implements UserRepository {
        private final Map<UUID, User> users = new HashMap<>();

        @Override
        public Optional<User> findById(UUID userId) {
            return Optional.ofNullable(users.get(userId));
        }

        @Override
        public Optional<User> findByEmail(String email) {
            return users.values().stream().filter(u -> u.getEmail().equals(email)).findFirst();
        }

        @Override
        public User save(User user) {
            users.put(user.getId(), user);
            return user;
        }
    }

    /** Fake hasher: stores "hash:" + raw so verification is deterministic and not real BCrypt. */
    private static final class FakePasswordHasher implements PasswordHasher {
        @Override
        public String hash(String rawPassword) {
            return "hash:" + rawPassword;
        }

        @Override
        public boolean matches(String rawPassword, String hashedPassword) {
            return ("hash:" + rawPassword).equals(hashedPassword);
        }
    }
}
