package org.infinity.passport.exception;

public class LoginUserNotExistException extends RuntimeException {

    private static final long serialVersionUID = 3389857462571862367L;

    private String            userName;

    private String            message;

    public LoginUserNotExistException(String userName) {
        super();
        this.userName = userName;
    }

    public LoginUserNotExistException(String userName, String message) {
        super();
        this.userName = userName;
        this.message = message;
    }

    public String getUserName() {
        return userName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ParameterizedErrorDTO getErrorDTO() {
        return new ParameterizedErrorDTO(message, userName);
    }
}
