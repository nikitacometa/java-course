package gitpro.exceptions;

/**
 * Created by wackloner on 22-Mar-17.
 */
public class GitPROException extends Exception {
    public GitPROException(String message) {
        super(message);
    }

    public GitPROException(String message, Throwable cause) {
        super(message, cause);
    }
}
