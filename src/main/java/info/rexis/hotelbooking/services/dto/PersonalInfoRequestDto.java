package info.rexis.hotelbooking.services.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PersonalInfoRequestDto {
    private String id;
    private String token;
}
