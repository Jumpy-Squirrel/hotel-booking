package info.rexis.hotelbooking.repositories.regsys.exceptions;

import lombok.Getter;

@Getter
public class RegsysServerError extends RuntimeException {
    private int httpStatus;
    public RegsysServerError(String message, int httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
