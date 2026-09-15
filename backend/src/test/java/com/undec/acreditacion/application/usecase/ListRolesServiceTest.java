package com.undec.acreditacion.application.usecase;

import com.undec.acreditacion.application.output.RoleRepository;
import com.undec.acreditacion.domain.entities.Role;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ListRolesServiceTest {

    @Test
    void returnsAllRolesFromRepository() {
        RoleRepository roleRepository = mock(RoleRepository.class);
        ListRolesService service = new ListRolesService(roleRepository);

        Role r1 = Role.system(UUID.randomUUID(), "R1", "Role 1", "Desc");
        when(roleRepository.findAll()).thenReturn(List.of(r1));

        List<Role> result = service.listRoles();
        assertEquals(1, result.size());
    }
}
