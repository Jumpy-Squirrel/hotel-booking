package info.rexis.hotelbooking.repositories.regsys;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PersonalInfoRequestDto {
    private int id;
    private String token;
}
