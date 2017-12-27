package info.rexis.hotelbooking.services.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PersonalInfoDto {
    private String name;
    private String street;
    private String city;
    private String email;
}
