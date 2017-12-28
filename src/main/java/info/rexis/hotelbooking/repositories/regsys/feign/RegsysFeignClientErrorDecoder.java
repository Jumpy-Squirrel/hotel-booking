package info.rexis.hotelbooking.repositories.regsys.feign;

import feign.FeignException;
import feign.Response;
import feign.codec.ErrorDecoder;
import info.rexis.hotelbooking.repositories.regsys.exceptions.RegsysClientError;
import info.rexis.hotelbooking.repositories.regsys.exceptions.RegsysServerError;

public class RegsysFeignClientErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.status() >= 400 && response.status() <= 499) {
            return new RegsysClientError(
                    response.reason(),
                    response.status()
            );
        }
        if (response.status() >= 500 && response.status() <= 599) {
            return new RegsysServerError(
                    response.reason(),
                    response.status()
            );
        }
        return FeignException.errorStatus(methodKey, response);
    }
}
