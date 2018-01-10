package org.infinity.passport.exception;

import java.io.Serializable;

/**
 * DTO for sending a parameterized error message.
 */
public class ParameterizedErrorDTO implements Serializable {

    private static final long serialVersionUID = -2060518823145626799L;

    private final String      message;

    private final Object[]    params;

    public ParameterizedErrorDTO(String message, Object... params) {
        this.message = message;
        this.params = params;
    }

    public String getMessage() {
        return message;
    }

    public Object[] getParams() {
        return params;
    }
}
