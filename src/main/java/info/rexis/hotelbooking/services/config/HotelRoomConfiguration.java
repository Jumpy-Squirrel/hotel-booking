package info.rexis.hotelbooking.services.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(HotelRoomProperties.class)
public class HotelRoomConfiguration {
}
