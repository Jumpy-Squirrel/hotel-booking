package info.rexis.hotelbooking.repositories.regsys.exceptions;

import info.rexis.hotelbooking.util.exceptions.StacktraceNotNeeded;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RegsysParameterError extends RuntimeException implements StacktraceNotNeeded {
    public RegsysParameterError(String message) {
        super(message);
    }
}
