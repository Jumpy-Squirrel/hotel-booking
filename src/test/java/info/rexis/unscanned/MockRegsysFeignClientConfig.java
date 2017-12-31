package info.rexis.unscanned;

import feign.Response;
import feign.codec.ErrorDecoder;
import info.rexis.hotelbooking.repositories.regsys.RegsysInfoResponse;
import info.rexis.hotelbooking.repositories.regsys.RegsysRepository;
import info.rexis.hotelbooking.repositories.regsys.feign.LoggingErrorDecoderWrapper;
import info.rexis.hotelbooking.repositories.regsys.feign.RegsysFeignClient;
import info.rexis.hotelbooking.repositories.regsys.feign.RegsysFeignClientErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.Collections;

@Configuration
public class MockRegsysFeignClientConfig {
    public static final String SERVERERROR_TOKEN = "servererror";
    public static final String CLIENTERROR_TOKEN = "clienterror";
    public static final String INVALID_TOKEN = "invalid";

    public static class MockRegsysFeignClient implements RegsysFeignClient {
        @Override
        public RegsysInfoResponse attendeeInfo(String auth, int id, String token) {
            ErrorDecoder errDec = new LoggingErrorDecoderWrapper(new RegsysFeignClientErrorDecoder());
            if (token == null || token.equals(CLIENTERROR_TOKEN)) {
                throw (RuntimeException) errDec.decode("whatever",
                        Response.builder()
                                .status(400)
                                .reason("missing parameter token")
                                .headers(Collections.emptyMap())
                                .build()
                );
            }
            if (token.equals(SERVERERROR_TOKEN)) {
                throw (RuntimeException) errDec.decode("whatever",
                        Response.builder()
                                .status(500)
                                .reason("server error")
                                .headers(Collections.emptyMap())
                                .build()
                );
            }

            RegsysInfoResponse regsysInfoResponse = new RegsysInfoResponse();
            if (token.equals(INVALID_TOKEN)) {
                regsysInfoResponse.setOk(false);
            } else {
                regsysInfoResponse.setOk(true);
            }
            return regsysInfoResponse;
        }
    }

    @Bean
    @Primary
    public RegsysRepository regsysRepository() {
        return new RegsysRepository(new MockRegsysFeignClient());
    }
}
