package com.undec.acreditacion.application.input;

import com.undec.acreditacion.domain.entities.User;

/**
 * Input port for the login use case.
 *
 * <p>Returns the authenticated {@link User} on success or throws
 * {@link com.undec.acreditacion.application.exception.AuthenticationFailedException}
 * for any failure (unknown user, wrong password, inactive user).</p>
 */
public interface LoginUseCase {

    User login(String email, String rawPassword);
}
