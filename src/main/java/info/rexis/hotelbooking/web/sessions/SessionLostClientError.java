package info.rexis.hotelbooking.web.sessions;

public class SessionLostClientError extends RuntimeException {
    public SessionLostClientError(String message) {
        super(message);
    }
}
