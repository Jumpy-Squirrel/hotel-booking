package info.rexis.hotelbooking.services;

import info.rexis.hotelbooking.services.config.HotelRoomProperties;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ReservationService {
    private HotelRoomProperties hotelRoomProperties;

    public HotelRoomProperties getHotelRoomProperties() {
        return hotelRoomProperties;
    }
}
