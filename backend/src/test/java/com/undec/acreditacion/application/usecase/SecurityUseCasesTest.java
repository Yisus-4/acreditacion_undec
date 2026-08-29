package com.undec.acreditacion.application.usecase;

import com.undec.acreditacion.application.output.RoleRepository;
import com.undec.acreditacion.application.output.UserRepository;
import com.undec.acreditacion.domain.entities.Role;
import com.undec.acreditacion.domain.entities.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/** Executable unit tests that do not require a test framework. */
public final class SecurityUseCasesTest {

    public static void main(String[] args) {
        registersAndSavesUser();
        rejectsDuplicateEmail();
        assignsRoleAndSavesUser();
        rejectsMissingRole();
        preservesDomainValidationWhenAssigningRole();
        activatesAndDeactivatesUser();
        rejectsMissingUser();
        rejectsNullIdentifiers();
    }

    private static void registersAndSavesUser() {
        InMemoryUserRepository users = new InMemoryUserRepository();
        User saved = new RegisterUserService(users).register("user", "user@example.org", "hash");

        check(saved == users.findById(saved.getId()).orElse(null), "registered user must be saved");
        check(saved.isActive(), "registered user must be active");
    }

    private static void assignsRoleAndSavesUser() {
        InMemoryUserRepository users = new InMemoryUserRepository();
        InMemoryRoleRepository roles = new InMemoryRoleRepository();
        User user = User.register("user", "user@example.org", "hash");
        Role role = Role.custom(UUID.randomUUID(), "EDITOR", "Editor", "Edits content");
        users.save(user);
        roles.save(role);

        new AssignRoleToUserService(users, roles).assignRole(user.getId(), role.getId());

        check(user.getRoles().contains(role), "role must be assigned through the domain entity");
        check(users.saveCount == 2, "assigned user must be saved");
    }

    private static void rejectsDuplicateEmail() {
        InMemoryUserRepository users = new InMemoryUserRepository();
        users.save(User.register("existing", "user@example.org", "hash"));

        try {
            new RegisterUserService(users).register("another", "user@example.org", "hash");
            throw new AssertionError("duplicate email must be rejected");
        } catch (IllegalArgumentException expected) {
            check(users.saveCount == 1, "duplicate user must not be saved");
        }
    }

    private static void activatesAndDeactivatesUser() {
        InMemoryUserRepository users = new InMemoryUserRepository();
        User user = User.register("user", "user@example.org", "hash");
        users.save(user);

        new DeactivateUserService(users).deactivate(user.getId());
        check(!user.isActive(), "user must be deactivated");
        new ActivateUserService(users).activate(user.getId());
        check(user.isActive(), "user must be activated");
        check(users.saveCount == 3, "state changes must be saved");
    }

    private static void rejectsMissingRole() {
        InMemoryUserRepository users = new InMemoryUserRepository();
        User user = User.register("user", "user@example.org", "hash");
        users.save(user);

        try {
            new AssignRoleToUserService(users, new InMemoryRoleRepository())
                    .assignRole(user.getId(), UUID.randomUUID());
            throw new AssertionError("missing role must be rejected");
        } catch (IllegalArgumentException expected) {
            check(users.saveCount == 1, "user must not be saved when the role is missing");
        }
    }

    private static void preservesDomainValidationWhenAssigningRole() {
        InMemoryUserRepository users = new InMemoryUserRepository();
        InMemoryRoleRepository roles = new InMemoryRoleRepository();
        User user = User.register("user", "user@example.org", "hash");
        Role role = Role.custom(UUID.randomUUID(), "EDITOR", "Editor", "Edits content");
        user.deactivate();
        users.save(user);
        roles.save(role);

        try {
            new AssignRoleToUserService(users, roles).assignRole(user.getId(), role.getId());
            throw new AssertionError("inactive user must not receive a role");
        } catch (IllegalArgumentException expected) {
            check(user.getRoles().isEmpty(), "application service must delegate role validation to User");
            check(users.saveCount == 1, "user must not be saved after domain validation fails");
        }
    }

    private static void rejectsMissingUser() {
        try {
            new ActivateUserService(new InMemoryUserRepository()).activate(UUID.randomUUID());
            throw new AssertionError("missing user must be rejected");
        } catch (IllegalArgumentException expected) {
            // Expected application boundary error.
        }
    }

    private static void rejectsNullIdentifiers() {
        InMemoryUserRepository users = new InMemoryUserRepository();
        InMemoryRoleRepository roles = new InMemoryRoleRepository();

        expectNullPointer(() -> new AssignRoleToUserService(users, roles)
                .assignRole(null, UUID.randomUUID()), "null user id must be rejected");
        expectNullPointer(() -> new AssignRoleToUserService(users, roles)
                .assignRole(UUID.randomUUID(), null), "null role id must be rejected");
    }

    private static void expectNullPointer(Runnable action, String message) {
        try {
            action.run();
            throw new AssertionError(message);
        } catch (NullPointerException expected) {
            // Expected required-value validation.
        }
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static final class InMemoryUserRepository implements UserRepository {
        private final Map<UUID, User> users = new HashMap<>();
        private int saveCount;

        @Override
        public Optional<User> findById(UUID userId) {
            return Optional.ofNullable(users.get(userId));
        }

        @Override
        public Optional<User> findByEmail(String email) {
            return users.values().stream()
                    .filter(user -> user.getEmail().equals(email))
                    .findFirst();
        }

        @Override
        public User save(User user) {
            saveCount++;
            users.put(user.getId(), user);
            return user;
        }
    }

    private static final class InMemoryRoleRepository implements RoleRepository {
        private final Map<UUID, Role> roles = new HashMap<>();

        @Override
        public Optional<Role> findById(UUID roleId) {
            return Optional.ofNullable(roles.get(roleId));
        }

        @Override
        public Optional<Role> findByCode(String code) {
            return roles.values().stream().filter(role -> role.getCode().equals(code)).findFirst();
        }

        @Override
        public Role save(Role role) {
            roles.put(role.getId(), role);
            return role;
        }
    }
}
