package info.rexis.hotelbooking.repositories.regsys.exceptions;

public class RegsysClientError extends RuntimeException {
    private int httpStatus;

    public RegsysClientError(String message) {
        super(message);
    }

    public RegsysClientError(String message, Throwable cause) {
        super(message, cause);
    }

    public RegsysClientError(String message, int httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
