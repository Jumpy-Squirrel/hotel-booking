package info.rexis.hotelbooking.repositories.regsys.exceptions;

import info.rexis.hotelbooking.util.exceptions.StacktraceNotNeeded;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class RegsysAuthError extends RuntimeException implements StacktraceNotNeeded {
    public RegsysAuthError(String message) {
        super(message);
    }
}
