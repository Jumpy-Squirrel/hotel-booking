package info.rexis.hotelbooking.web.mappers;

import info.rexis.hotelbooking.services.config.HotelRoomProperties;
import info.rexis.hotelbooking.services.dto.ReservationDto;
import info.rexis.hotelbooking.util.mappers.DateConverter;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ReservationMapper {
    private DateConverter dateConverter;

    public void modelFromReservation(Model model, ReservationDto reservation, HotelRoomProperties roomtypes) {
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
        model.addAttribute("roomtype", roomtypeValue(reservation, roomtypes));

        model.addAttribute("arrival", arrivalConverted(reservation));
        model.addAttribute("departure", departureConverted(reservation));

        model.addAttribute("comments", reservation.getComments());
    }

    public Map<String, String> listviewMapFromReservation(ReservationDto reservation, HotelRoomProperties roomtypes) {
        Map<String, String> result = new HashMap<>();
        result.put("pk", reservation.getPk());
        result.put("name1", reservation.getName1());
        result.put("email1", reservation.getEmail1());
        result.put("roomsize", Integer.toString(reservation.getRoomsize()));
        result.put("roomtype", roomtypeValue(reservation, roomtypes));
        result.put("arrival", arrivalConverted(reservation));
        result.put("departure", departureConverted(reservation));
        return result;
    }

    private String arrivalConverted(ReservationDto reservation) {
        return localeConvert(reservation.getArrival(), reservation.getDateformat());
    }

    private String departureConverted(ReservationDto reservation) {
        return localeConvert(reservation.getDeparture(), reservation.getDateformat());
    }

    private String roomtypeValue(ReservationDto reservation, HotelRoomProperties roomtypes) {
        try {
            return roomtypes.getRoomtypes().get(reservation.getRoomtype() - 1).getValue();
        } catch (Exception e) {
            return "-unknown-";
        }
    }

    private String localeConvert(String input, String inputformat) {
        if (input == null || input.equals("")) {
            return input;
        }

        if ("de".equals(currentLanguage())) {
            return dateConverter.localeConvert(input, inputformat, "dd.mm.yyyy");
        } else {
            return dateConverter.localeConvert(input, inputformat, "mm/dd/yyyy");
        }
    }

    protected String currentLanguage() {
        Locale locale = LocaleContextHolder.getLocale();
        return locale.getLanguage();
    }
}
