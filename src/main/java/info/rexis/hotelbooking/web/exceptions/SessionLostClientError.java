package info.rexis.hotelbooking.web.exceptions;

public class SessionLostClientError extends RuntimeException {
    private int httpStatus;

    public SessionLostClientError(String message) {
        super(message);
    }

    public SessionLostClientError(String message, int httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
