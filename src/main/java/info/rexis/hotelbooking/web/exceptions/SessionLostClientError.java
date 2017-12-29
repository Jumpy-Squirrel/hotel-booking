package info.rexis.hotelbooking.web.exceptions;

public class SessionLostClientError extends RuntimeException {
    public SessionLostClientError(String message) {
        super(message);
    }
}
