package info.rexis.hotelbooking.repositories.regsys.exceptions;

public class RegsysServerError extends RuntimeException {
    private int httpStatus;

    public RegsysServerError(String message) {
        super(message);
    }

    public RegsysServerError(String message, Throwable cause) {
        super(message, cause);
    }

    public RegsysServerError(String message, int httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
