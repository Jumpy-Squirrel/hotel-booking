package info.rexis.hotelbooking.repositories.regsys.exceptions;

import lombok.Getter;

@Getter
public class RegsysClientError extends RuntimeException {
    private int httpStatus;
    public RegsysClientError(String message, int httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
