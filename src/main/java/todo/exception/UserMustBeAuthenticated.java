package todo.exception;

public class UserMustBeAuthenticated extends RuntimeException {
    public UserMustBeAuthenticated(String message) {
        super(message);
    }
}
