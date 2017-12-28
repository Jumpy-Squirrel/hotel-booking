package info.rexis.hotelbooking.web;

import info.rexis.hotelbooking.repositories.regsys.exceptions.RegsysClientError;
import info.rexis.hotelbooking.services.ReservationService;
import info.rexis.hotelbooking.services.dto.EmailDto;
import info.rexis.hotelbooking.services.dto.PersonalInfoDto;
import info.rexis.hotelbooking.services.dto.PersonalInfoRequestDto;
import info.rexis.hotelbooking.services.dto.ReservationDto;
import info.rexis.hotelbooking.web.exceptions.SessionLostClientError;
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

@Controller
@RequestMapping("/")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class WebViewController {
    public static final String PAGE_MAIN = "main";
    public static final String PAGE_FORM = "reservation-form";
    public static final String PAGE_SHOW = "reservation-show";
    public static final String PAGE_FORBIDDEN = "forbidden";

    private ReservationService reservationService;

    @GetMapping
    public String showMainPage(@RequestParam(name = "id", required = false, defaultValue = "0") String id,
                               @RequestParam(name = "token", required =  false, defaultValue = "") String token,
                               HttpSession session,
                               Model model) {
        PersonalInfoRequestDto piRequest = PersonalInfoRequestDto.builder()
                .id(Integer.parseInt(id))
                .token(token)
                .build();
        PersonalInfoDto personalInfo = reservationService.requestPersonalInfo(piRequest);
        putPersonalInfoIntoSession(session, personalInfo);
        return showPage(PAGE_MAIN, model);
    }

    @GetMapping(PAGE_MAIN)
    public String showMainPageAgain(HttpSession session, Model model) {
        PersonalInfoDto personalInfo = getPersonalInfoFromSession(session);
        session.setAttribute("personal", personalInfo);
        return showPage(PAGE_MAIN, model);
    }

    @GetMapping(PAGE_FORM)
    public String showReservationFormPage(HttpSession session, Model model) {
        PersonalInfoDto personalInfo = getPersonalInfoFromSession(session);
        setupForm(model, personalInfo);
        return showPage(PAGE_FORM, model);
    }

    @PostMapping(PAGE_SHOW)
    public String showReservationShowPage(@ModelAttribute("reservation") ReservationDto reservation,
                                          HttpSession session,
                                          Model model) {
        PersonalInfoDto personalInfo = getPersonalInfoFromSession(session);
        EmailDto emailDto = reservationService.constructEmail(reservation, personalInfo);
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
        return showPage(PAGE_SHOW, model);
    }

    @ExceptionHandler(RegsysClientError.class)
    public String showForbiddenPage(Model model) {
        return showPage(PAGE_FORBIDDEN, model);
    }

    @ExceptionHandler(SessionLostClientError.class)
    public String showSessionLostPage(Model model) {
        return showPage(PAGE_FORBIDDEN, model);
    }

    private void putPersonalInfoIntoSession(HttpSession session, PersonalInfoDto personalInfo) {
        session.setAttribute("personal", personalInfo);
    }

    private PersonalInfoDto getPersonalInfoFromSession(HttpSession session) {
        PersonalInfoDto personalInfo = (PersonalInfoDto) session.getAttribute("personal");
        if (personalInfo == null) {
            throw new SessionLostClientError("session info not available - either wrong entry point or lost session cookie");
        }
        return personalInfo;
    }

    private String showPage(String page, Model model) {
        model.addAttribute("currentpage", page);
        return page;
    }

    private void setupForm(Model model, PersonalInfoDto info) {
        model.addAttribute("roomsize", "1");
        model.addAttribute("name1", info.getName());
        model.addAttribute("street1", info.getStreet());
        model.addAttribute("city1", info.getCity());
        model.addAttribute("email1", info.getEmail());
        model.addAttribute("roomtype", "standard");
        model.addAttribute("roomtypes", reservationService.getHotelRoomProperties().toListOfMaps());
    }
}
