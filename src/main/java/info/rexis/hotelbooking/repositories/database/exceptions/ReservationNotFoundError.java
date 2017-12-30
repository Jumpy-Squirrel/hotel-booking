package info.rexis.hotelbooking.repositories.database.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ReservationNotFoundError extends RuntimeException {
    public ReservationNotFoundError(String message) {
        super(message);
    }

    public ReservationNotFoundError(String message, Throwable cause) {
        super(message, cause);
    }
}
