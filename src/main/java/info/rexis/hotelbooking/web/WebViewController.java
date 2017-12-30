package info.rexis.hotelbooking.web;

import info.rexis.hotelbooking.repositories.regsys.exceptions.RegsysAuthError;
import info.rexis.hotelbooking.services.ReservationService;
import info.rexis.hotelbooking.services.dto.EmailDto;
import info.rexis.hotelbooking.services.dto.PersonalInfoDto;
import info.rexis.hotelbooking.services.dto.ReservationDto;
import info.rexis.hotelbooking.web.sessions.SessionLostClientError;
import info.rexis.hotelbooking.web.mappers.ReservationMapper;
import info.rexis.hotelbooking.web.sessions.PersonalInfoSessionStore;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Making reservation requests, the public part of the user interface.
 */
@Controller
@RequestMapping("/")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class WebViewController {
    public static final String PAGE_MAIN = "main";
    public static final String PAGE_FORM = "reservation-form";
    public static final String PAGE_SHOW = "reservation-show";
    public static final String PAGE_FORBIDDEN = "forbidden";
    public static final String PAGE_SESSION_LOST = "session-lost";

    private ReservationService reservationService;
    private ReservationMapper reservationMapper;
    private PersonalInfoSessionStore sessionStore;

    @GetMapping
    public String showMainPage(@RequestParam(name = "id", required = false, defaultValue = "0") String id,
                               @RequestParam(name = "token", required =  false, defaultValue = "") String token,
                               HttpSession session,
                               Model model) {
        PersonalInfoDto personalInfo = reservationService.requestPersonalInfo(Integer.parseInt(id), token);
        sessionStore.putPersonalInfoIntoSession(session, personalInfo);

        return showPage(PAGE_MAIN, model, true);
    }

    @GetMapping(PAGE_MAIN)
    public String showMainPageAgain(HttpSession session, Model model) {
        sessionStore.getPersonalInfoFromSession(session);

        return showPage(PAGE_MAIN, model, true);
    }

    @GetMapping(PAGE_FORM)
    public String showReservationFormPage(HttpSession session, Model model) {
        PersonalInfoDto personalInfo = sessionStore.getPersonalInfoFromSession(session);
        ReservationDto reservation = reservationService.prefillReservation(personalInfo);

        reservationMapper.modelFromReservation(model, reservation, reservationService.getHotelRoomProperties());
        model.addAttribute("roomtypes", reservationService.getHotelRoomProperties().toListOfMaps());
        return showPage(PAGE_FORM, model, true);
    }

    @PostMapping(PAGE_SHOW)
    public String showReservationShowPage(@ModelAttribute("reservation") ReservationDto reservation,
                                          HttpSession session,
                                          Model model) {
        PersonalInfoDto personalInfo = sessionStore.getPersonalInfoFromSession(session);
        EmailDto emailDto = reservationService.constructEmail(reservation, personalInfo);
        reservationService.saveSubmittedReservation(reservation);

        model.addAttribute("email", emailDto.getBody());
        model.addAttribute("subject", emailDto.getSubject());
        model.addAttribute("recipient", emailDto.getRecipient());
        try {
            String encodedBody = URLEncoder.encode(emailDto.getBody(), StandardCharsets.UTF_8.toString());
            String encodedSubject = URLEncoder.encode(emailDto.getSubject(), StandardCharsets.UTF_8.toString());
            String fullMailtoLink = "mailto:" + emailDto.getRecipient() + "?subject=" + encodedSubject + "&body=" + encodedBody;
            model.addAttribute("mailtolink", fullMailtoLink);
        } catch (Exception ignore) {
            // ok, we won't offer the link in this case
        }
        return showPage(PAGE_SHOW, model, true);
    }

    @ExceptionHandler(RegsysAuthError.class)
    public String showForbiddenPage(Model model) {
        return showPage(PAGE_FORBIDDEN, model, false);
    }

    @ExceptionHandler(SessionLostClientError.class)
    public String showSessionLostPage(Model model) {
        return showPage(PAGE_SESSION_LOST, model, false);
    }

    private String showPage(String page, Model model, boolean customJs) {
        model.addAttribute("currentpage", page);
        if (customJs) {
            model.addAttribute("includecustomjs", "true");
        }
        return page;
    }
}
