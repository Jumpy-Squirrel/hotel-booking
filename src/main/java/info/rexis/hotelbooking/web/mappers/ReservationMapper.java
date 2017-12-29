package info.rexis.hotelbooking.web.mappers;

import info.rexis.hotelbooking.services.config.HotelRoomProperties;
import info.rexis.hotelbooking.services.dto.ReservationDto;
import org.springframework.ui.Model;

public class ReservationMapper {
    public static void modelFromReservation(Model model, ReservationDto reservation, HotelRoomProperties roomtypes) {
        model.addAttribute("name1", reservation.getName1());
        model.addAttribute("street1", reservation.getStreet1());
        model.addAttribute("city1", reservation.getCity1());
        model.addAttribute("country1", reservation.getCountry1());
        model.addAttribute("email1", reservation.getEmail1());

        model.addAttribute("name2", reservation.getName2());
        model.addAttribute("street2", reservation.getStreet2());
        model.addAttribute("city2", reservation.getCity2());
        model.addAttribute("country2", reservation.getCountry2());
        model.addAttribute("email2", reservation.getEmail2());

        model.addAttribute("name3", reservation.getName3());
        model.addAttribute("street3", reservation.getStreet3());
        model.addAttribute("city3", reservation.getCity3());
        model.addAttribute("country3", reservation.getCountry3());
        model.addAttribute("email3", reservation.getEmail3());

        model.addAttribute("roomsize", Integer.toString(reservation.getRoomsize()));
        model.addAttribute("roomtype", roomtypes.getRoomtypes().get(reservation.getRoomtype() - 1).getValue());
        model.addAttribute("comments", reservation.getComments());
    }
}
