package info.rexis.hotelbooking.web;

import info.rexis.hotelbooking.repositories.regsys.RegsysInfoResponse;
import info.rexis.hotelbooking.repositories.regsys.RegsysRepository;
import info.rexis.hotelbooking.repositories.regsys.feign.RegsysFeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class MockRegsysFeignClientConfig {
    public RegsysFeignClient mockRegsysFeignClient() {
        return (auth, id, token) -> {
            RegsysInfoResponse regsysInfoResponse = new RegsysInfoResponse();
            regsysInfoResponse.setOk(true);
            return regsysInfoResponse;
        };
    }

    @Bean
    @Primary
    public RegsysRepository regsysRepository() {
        return new RegsysRepository(mockRegsysFeignClient());
    }
}
