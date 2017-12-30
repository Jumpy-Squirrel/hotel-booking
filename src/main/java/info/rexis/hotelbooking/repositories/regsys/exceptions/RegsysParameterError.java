package info.rexis.hotelbooking.repositories.regsys.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RegsysParameterError extends RuntimeException {
    public RegsysParameterError(String message) {
        super(message);
    }

    public RegsysParameterError(String message, Throwable cause) {
        super(message, cause);
    }
}
