package info.rexis.hotelbooking.web.config;

import info.rexis.hotelbooking.web.AppErrorController;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ErrorControllerConfiguration {
    private ErrorAttributes errorAttributes;
    private RegexedRequestLoggingFilter requestLoggingFilter;

    @Bean
    public AppErrorController appErrorController() {
        return new AppErrorController(errorAttributes, requestLoggingFilter);
    }
}
