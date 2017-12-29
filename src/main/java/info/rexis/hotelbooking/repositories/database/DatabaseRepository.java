package info.rexis.hotelbooking.repositories.database;

import info.rexis.hotelbooking.repositories.database.internal.ReservationRepository;
import info.rexis.hotelbooking.repositories.database.util.PkGenerator;
import info.rexis.hotelbooking.services.dto.ReservationDto;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@Repository
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class DatabaseRepository {
    private ReservationRepository reservationRepository;

    public void saveReservation(ReservationDto reservation) {
        if (StringUtils.isEmpty(reservation.getPk())) {
            reservation.setPk(PkGenerator.generatePk(reservation.getId()));
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

    private int comparatorReverseSortedByPk(ReservationDto a, ReservationDto b) {
        String pka = nvl(a.getPk());
        String pkb = nvl(b.getPk());
        return pkb.compareTo(pka);
    }

    private String nvl(String v) {
        return v == null ? "" : v;
    }
}
