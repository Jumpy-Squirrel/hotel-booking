package info.rexis.hotelbooking.repositories.email;

import info.rexis.hotelbooking.services.dto.ReservationDto;
import org.springframework.stereotype.Component;

@Component
public class EmailRepository {
    public String mapToEmail(ReservationDto reservation) {
        // todo incomplete implementation
        return "To: somewhere@mailinator.com\n"
                + "Subject: Hotel Reservation\n\n"
        + "Room size:  " + reservation.getRoomsize() + "\n"
        + "Room type:  " + reservation.getRoomtype() + "\n\n";
    }
}
