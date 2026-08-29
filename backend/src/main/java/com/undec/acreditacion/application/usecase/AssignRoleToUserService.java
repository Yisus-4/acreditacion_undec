package com.undec.acreditacion.application.usecase;

import com.undec.acreditacion.application.input.AssignRoleToUserUseCase;
import com.undec.acreditacion.application.output.RoleRepository;
import com.undec.acreditacion.application.output.UserRepository;
import com.undec.acreditacion.domain.entities.Role;
import com.undec.acreditacion.domain.entities.User;

import java.util.Objects;
import java.util.UUID;

public final class AssignRoleToUserService implements AssignRoleToUserUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public AssignRoleToUserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = Objects.requireNonNull(userRepository, "User repository is required");
        this.roleRepository = Objects.requireNonNull(roleRepository, "Role repository is required");
    }

    @Override
    public void assignRole(UUID userId, UUID roleId) {
        UUID requiredUserId = requireId(userId, "User id");
        UUID requiredRoleId = requireId(roleId, "Role id");
        User user = findUser(requiredUserId);
        Role role = roleRepository.findById(requiredRoleId)
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado: " + roleId));

        user.assignRole(role);
        userRepository.save(user);
    }

    private User findUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + userId));
    }

    private static UUID requireId(UUID value, String field) {
        return Objects.requireNonNull(value, field + " es requerido");
    }
}
