package com.undec.acreditacion.application.usecase;

import com.undec.acreditacion.application.input.ListRolesUseCase;
import com.undec.acreditacion.application.output.RoleRepository;
import com.undec.acreditacion.domain.entities.Role;

import java.util.List;
import java.util.Objects;

public final class ListRolesService implements ListRolesUseCase {

    private final RoleRepository roleRepository;

    public ListRolesService(RoleRepository roleRepository) {
        this.roleRepository = Objects.requireNonNull(roleRepository, "Role repository is required");
    }

    @Override
    public List<Role> listRoles() {
        return roleRepository.findAll();
    }
}
