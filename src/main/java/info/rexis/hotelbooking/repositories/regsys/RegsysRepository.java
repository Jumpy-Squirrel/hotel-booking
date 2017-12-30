package info.rexis.hotelbooking.repositories.regsys;

import info.rexis.hotelbooking.repositories.regsys.exceptions.RegsysAuthError;
import info.rexis.hotelbooking.repositories.regsys.exceptions.RegsysParameterError;
import info.rexis.hotelbooking.repositories.regsys.feign.RegsysFeignClient;
import info.rexis.hotelbooking.services.dto.PersonalInfoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class RegsysRepository {
    @Value("${regsys.auth}")
    private String auth;

    private RegsysFeignClient regsysFeignClient;

    @Autowired
    public RegsysRepository(RegsysFeignClient regsysFeignClient) {
        this.regsysFeignClient = regsysFeignClient;
    }

    public PersonalInfoDto getPersonalInfo(PersonalInfoRequestDto personalInfoRequest) {
        return mapAndRequest(regsysFeignClient, personalInfoRequest);
    }

    private PersonalInfoDto mapAndRequest(RegsysFeignClient client, PersonalInfoRequestDto personalInfoRequest) {
        if (personalInfoRequest.getId() <= 0 || personalInfoRequest.getToken() == null || personalInfoRequest.getToken().equals("")) {
            throw new RegsysParameterError("No valid id provided or no token provided");
        }
        RegsysInfoResponse response = client.attendeeInfo(auth, personalInfoRequest.getId(), personalInfoRequest.getToken());
        if (!response.isOk()) {
            throw new RegsysAuthError("Could not retrieve attendee data from regsys, probably wrong auth, id, token or attendee status");
        }
        return constructPersonalInfoDto(personalInfoRequest, response);
    }

    private PersonalInfoDto constructPersonalInfoDto(PersonalInfoRequestDto personalInfoRequest, RegsysInfoResponse response) {
        return PersonalInfoDto.builder()
                .id(personalInfoRequest.getId())
                .token(personalInfoRequest.getToken())
                .name(response.getName())
                .street(response.getStreet())
                .city(response.getCity())
                .country(response.getCountry())
                .email(response.getEmail())
                .build();
    }
}
