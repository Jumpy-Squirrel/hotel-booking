package info.rexis.hotelbooking.web.mappers;

import info.rexis.hotelbooking.services.config.HotelRoomProperties;
import info.rexis.hotelbooking.services.dto.ReservationDto;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.util.Locale;

@Component
public class ReservationMapper {
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
        model.addAttribute("roomtype", roomtypes.getRoomtypes().get(reservation.getRoomtype() - 1).getValue());

        model.addAttribute("arrival", localeConvert(reservation.getArrival(), reservation.getDateformat()));
        model.addAttribute("departure", localeConvert(reservation.getDeparture(), reservation.getDateformat()));

        model.addAttribute("comments", reservation.getComments());
    }

    private String localeConvert(String input, String inputformat) {
        if (input == null || input.equals("")) {
            return input;
        }

        if ("de".equals(currentLanguage())) {
            return localeConvert(input, inputformat, "dd.mm.yyyy");
        } else {
            return localeConvert(input, inputformat, "mm/dd/yyyy");
        }
    }

    protected String localeConvert(String input, String inputformat, String outputformat) {
        String iregex = inputformat
                .replaceFirst("dd", "([0-9]{2})")
                .replaceFirst("mm", "([0-9]{2})")
                .replaceFirst("yyyy", "([0-9]{4})")
                .replaceAll("\\.", "\\.");
        String daysAre = "\\$1";
        String monthsAre = "\\$2";
        if (inputformat.matches("mm.dd.yyyy")) {
            daysAre = "\\$2";
            monthsAre = "\\$1";
        }
        String oregex = outputformat
                .replaceFirst("dd", daysAre)
                .replaceFirst("mm", monthsAre)
                .replaceFirst("yyyy", "\\$3");
        return input.replaceFirst(iregex, oregex);
    }

    protected String currentLanguage() {
        Locale locale = LocaleContextHolder.getLocale();
        return locale.getLanguage();
    }
}
