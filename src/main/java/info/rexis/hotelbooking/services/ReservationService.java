package info.rexis.hotelbooking.services;

import info.rexis.hotelbooking.repositories.email.EmailRepository;
import info.rexis.hotelbooking.repositories.regsys.RegsysRepository;
import info.rexis.hotelbooking.services.dto.EmailDto;
import info.rexis.hotelbooking.services.dto.PersonalInfoDto;
import info.rexis.hotelbooking.services.dto.PersonalInfoRequestDto;
import info.rexis.hotelbooking.services.config.HotelRoomProperties;
import info.rexis.hotelbooking.services.dto.ReservationDto;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ReservationService {
    private HotelRoomProperties hotelRoomProperties;
    private EmailRepository emailRepository;
    private RegsysRepository regsysRepository;

    public HotelRoomProperties getHotelRoomProperties() {
        return hotelRoomProperties;
    }

    public PersonalInfoDto requestPersonalInfo(PersonalInfoRequestDto infoRequest) {
        return regsysRepository.getPersonalInfo(infoRequest);
    }

    public EmailDto constructEmail(ReservationDto reservation, PersonalInfoDto personalInfo) {
        reservation.setId(personalInfo.getId());
        reservation.setToken(personalInfo.getToken());
        // in case they have set the 1st person values somehow, we overwrite them again
        reservation.setName1(personalInfo.getName());
        reservation.setStreet1(personalInfo.getStreet());
        reservation.setCity1(personalInfo.getCity());
        reservation.setEmail1(personalInfo.getEmail());
        return emailRepository.mapToEmail(reservation, hotelRoomProperties);
    }
}
