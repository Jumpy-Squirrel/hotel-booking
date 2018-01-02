package info.rexis.hotelbooking.services;

import info.rexis.hotelbooking.repositories.database.DatabaseRepository;
import info.rexis.hotelbooking.repositories.database.exceptions.ConcurrentLockError;
import info.rexis.hotelbooking.repositories.email.EmailRepository;
import info.rexis.hotelbooking.repositories.regsys.RegsysRepository;
import info.rexis.hotelbooking.services.config.HotelRoomProperties;
import info.rexis.hotelbooking.services.dto.EmailDto;
import info.rexis.hotelbooking.services.dto.PersonalInfoDto;
import info.rexis.hotelbooking.repositories.regsys.PersonalInfoRequestDto;
import info.rexis.hotelbooking.services.dto.ProcessStatus;
import info.rexis.hotelbooking.services.dto.ReservationDto;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ReservationService {
    private HotelRoomProperties hotelRoomProperties;
    private EmailRepository emailRepository;
    private RegsysRepository regsysRepository;
    private DatabaseRepository databaseRepository;

    public HotelRoomProperties getHotelRoomProperties() {
        return hotelRoomProperties;
    }

    public PersonalInfoDto requestPersonalInfo(int id, String token) {
        PersonalInfoRequestDto infoRequest = PersonalInfoRequestDto.builder()
                .id(id)
                .token(token)
                .build();
        return regsysRepository.getPersonalInfo(infoRequest);
    }

    public ReservationDto prefillReservation(PersonalInfoDto personalInfo) {
        ReservationDto fromDatabase = databaseRepository.findLatestReservationOrNull(personalInfo.getId());
        if (fromDatabase != null) {
            return fromDatabase;
        }

        ReservationDto fresh = new ReservationDto();
        fillInOverwriteFirstPerson(fresh, personalInfo);
        fresh.setRoomsize(1);
        fresh.setRoomtype(1);
        return fresh;
    }

    @Transactional
    public ReservationDto fetchAndLockForProcessingOrThrow(String pk, String sessionId) {
        databaseRepository.lockReservationForProcessingOrNull(pk, ProcessStatus.NEW, ProcessStatus.PROCESSING, sessionId);

        ReservationDto reservation = databaseRepository.loadReservationOrThrow(pk);
        if (reservation.getStatus() == ProcessStatus.PROCESSING && sessionId.equals(reservation.getSession())) {
            // we got it
            return reservation;
        } else {
            throw new ConcurrentLockError();
        }
    }

    public void releaseAndPutBack(String pk) {
        ReservationDto reservation = databaseRepository.loadReservationOrThrow(pk);
        reservation.setStatus(ProcessStatus.NEW);
        reservation.setSession(null);
        databaseRepository.saveReservation(reservation);
    }

    public void releaseAndSetToDone(String pk) {
        ReservationDto reservation = databaseRepository.loadReservationOrThrow(pk);
        reservation.setStatus(ProcessStatus.DONE);
        reservation.setProcessed(new Date());
        databaseRepository.saveReservation(reservation);
    }

    public void saveSubmittedReservation(ReservationDto reservation) {
        databaseRepository.saveReservation(reservation);
    }

    public EmailDto constructEmail(ReservationDto reservation, PersonalInfoDto personalInfo) {
        fillInOverwriteFirstPerson(reservation, personalInfo);
        return emailRepository.mapToEmail(reservation, hotelRoomProperties);
    }

    public List<ReservationDto> getEarliest20ByStatus(ProcessStatus status) {
        return databaseRepository.findEarliest20ByStatus(status);
    }

    private void fillInOverwriteFirstPerson(ReservationDto reservation, PersonalInfoDto personalInfo) {
        reservation.setId(personalInfo.getId());
        reservation.setToken(personalInfo.getToken());
        // in case they have set the 1st person values somehow, we overwrite them again
        reservation.setName1(personalInfo.getName());
        reservation.setStreet1(personalInfo.getStreet());
        reservation.setCity1(personalInfo.getCity());
        reservation.setCountry1(personalInfo.getCountry());
        reservation.setEmail1(personalInfo.getEmail());
    }
}
