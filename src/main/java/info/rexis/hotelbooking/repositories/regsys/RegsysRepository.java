package info.rexis.hotelbooking.repositories.regsys;

import info.rexis.hotelbooking.services.dto.PersonalInfoDto;
import info.rexis.hotelbooking.services.dto.PersonalInfoRequestDto;
import org.springframework.stereotype.Repository;

@Repository
public class RegsysRepository {
    public PersonalInfoDto getPersonalInfo(PersonalInfoRequestDto personalInfoRequest) {
        // this should make a request to the regsys using id and token from personalInfoRequest as taken from the session (and initially set)
        return PersonalInfoDto.builder()
                .name("First Last")
                .street("166 Main Street")
                .city("New York 1F8 4G3")
                .email("test@mailinator.com")
                .build();
    }
}
