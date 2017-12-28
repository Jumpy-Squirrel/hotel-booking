package info.rexis.hotelbooking.repositories.email;

import info.rexis.hotelbooking.services.config.HotelRoomProperties;
import info.rexis.hotelbooking.services.dto.EmailDto;
import info.rexis.hotelbooking.services.dto.ReservationDto;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Repository;

import java.util.Locale;

@Repository
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class EmailRepository {
    private VelocityRenderer renderer;

    public EmailDto mapToEmail(ReservationDto reservation, HotelRoomProperties roomInfos) {
        VelocityVariables vars = buildContext(reservation, roomInfos);
        String templatePath = buildTemplatePath("");
        String templatePathSubject = buildTemplatePath("_subject");

        EmailDto email = new EmailDto();
        email.setRecipient(roomInfos.getRecipient());
        email.setSubject(renderer.renderTemplate(templatePathSubject, vars));
        email.setBody(renderer.renderTemplate(templatePath, vars));
        return email;
    }

    private String buildTemplatePath(String component) {
        Locale locale = LocaleContextHolder.getLocale();
        return "emails/reservation_" + locale.getLanguage() + component + ".vm";
    }

    private VelocityVariables buildContext(ReservationDto reservation, HotelRoomProperties roomInfos) {
        VelocityVariables vars = new VelocityVariables();
        vars.put("r", reservation);
        vars.put("id", String.format("%06d", reservation.getId()));
        vars.put("roomtype", roomInfos.byRoomTypePosition(reservation.getRoomtype()).getDescription());
        vars.put("stichwort", roomInfos.getStichwort());
        return vars;
    }
}
