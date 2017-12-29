package info.rexis.hotelbooking.util.feign;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.AllArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@AllArgsConstructor
public class LoggingErrorDecoderWrapper implements ErrorDecoder {
    protected final Log logger = LogFactory.getLog(getClass());

    private ErrorDecoder wrappedErrorDecoder;

    @Override
    public Exception decode(String methodKey, Response response) {
        logger.warn(String.format("Feign received error response (methodKey: %s, Status: %d)", methodKey, response.status()));
        Exception e = wrappedErrorDecoder.decode(methodKey, response);
        logger.warn("Feign error converted into exception: " + e.getMessage());
        return e;
    }
}
