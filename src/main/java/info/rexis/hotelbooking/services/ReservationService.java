package info.rexis.hotelbooking.services;

import info.rexis.hotelbooking.repositories.email.EmailRepository;
import info.rexis.hotelbooking.repositories.regsys.RegsysRepository;
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

    public String constructEmail(ReservationDto reservation, PersonalInfoRequestDto personalInfoCredentials) {
        PersonalInfoDto personalInfo = regsysRepository.getPersonalInfo(personalInfoCredentials);
        reservation.overridePersonalInfo(personalInfo);
        return emailRepository.mapToEmail(reservation);
    }
}
