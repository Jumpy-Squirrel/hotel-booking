package info.rexis.hotelbooking.repositories.regsys.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class RegsysAuthError extends RuntimeException {
    public RegsysAuthError(String message) {
        super(message);
    }

    public RegsysAuthError(String message, Throwable cause) {
        super(message, cause);
    }
}
