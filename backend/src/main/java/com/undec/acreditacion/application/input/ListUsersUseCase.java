package com.undec.acreditacion.application.input;

import com.undec.acreditacion.domain.entities.User;

import java.util.List;

public interface ListUsersUseCase {

    List<User> listUsers();
}
