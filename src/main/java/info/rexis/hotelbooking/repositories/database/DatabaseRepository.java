package info.rexis.hotelbooking.repositories.database;

import info.rexis.hotelbooking.repositories.database.exceptions.ReservationNotFoundError;
import info.rexis.hotelbooking.repositories.database.internal.ReservationRepository;
import info.rexis.hotelbooking.repositories.database.util.PkGenerator;
import info.rexis.hotelbooking.services.dto.ProcessStatus;
import info.rexis.hotelbooking.services.dto.ReservationDto;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

@Repository
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class DatabaseRepository {
    private ReservationRepository reservationRepository;

    public ReservationDto loadReservationOrThrow(String pk) {
        ReservationDto result;
        try {
            result = reservationRepository.findByPk(pk);
        } catch (Exception e) {
            throw new ReservationNotFoundError("database error", e);
        }
        if (result == null) {
            throw new ReservationNotFoundError("no such entry for pk " + pk);
        }
        return result;
    }

    public void saveReservation(ReservationDto reservation) {
        if (StringUtils.isEmpty(reservation.getPk())) {
            reservation.setPk(PkGenerator.generatePk(reservation.getId()));
        }
        if (reservation.getStatus() == null) {
            reservation.setStatus(ProcessStatus.NEW);
        }
        reservationRepository.save(reservation);
    }

    public ReservationDto findLatestReservationOrNull(int id) {
        List<ReservationDto> reservations = reservationRepository.findById(id);
        if (reservations == null || reservations.isEmpty()) {
            return null;
        }
        reservations.sort(this::comparatorReverseSortedByPk);
        return reservations.get(0);
    }

    public List<ReservationDto> findEarliest20ByStatus(ProcessStatus status) {
        return reservationRepository.findTop20ByStatusOrderByPk(status);
    }

    public void lockReservationForProcessingOrNull(String pk, ProcessStatus oldstatus, ProcessStatus newstatus, String sessionId) {
        reservationRepository.lockReservationForProcessing(pk, oldstatus, newstatus, sessionId, new Date());
    }

    protected int comparatorReverseSortedByPk(ReservationDto a, ReservationDto b) {
        String pka = nvl(a.getPk());
        String pkb = nvl(b.getPk());
        return pkb.compareTo(pka);
    }

    private String nvl(String v) {
        return v == null ? "" : v;
    }
}
