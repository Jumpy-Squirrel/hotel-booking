package info.rexis.hotelbooking.services.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PersonalInfoDto {
    private int id;
    private String token;
    private String name;
    private String street;
    private String city;
    private String country;
    private String email;
}
