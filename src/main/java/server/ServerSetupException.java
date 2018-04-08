package server;

/**
 *
 * @author stuar
 */
public class ServerSetupException extends RuntimeException {

    public ServerSetupException(String message) {
        super(message);
    }

    public ServerSetupException(String message, Throwable cause) {
        super(message, cause);
    }

}
