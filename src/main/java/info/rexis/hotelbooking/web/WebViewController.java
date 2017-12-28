package info.rexis.hotelbooking.web;

import info.rexis.hotelbooking.services.ReservationService;
import info.rexis.hotelbooking.services.dto.EmailDto;
import info.rexis.hotelbooking.services.dto.PersonalInfoRequestDto;
import info.rexis.hotelbooking.services.dto.ReservationDto;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class WebViewController {
    public static final String PAGE_MAIN = "main";
    public static final String PAGE_FORM = "reservation-form";
    public static final String PAGE_SHOW = "reservation-show";

    private ReservationService reservationService;

    @GetMapping
    public String showMainPage(Model model) {
        return showPage(PAGE_MAIN, model);
    }

    @GetMapping(PAGE_FORM)
    public String showReservationFormPage(Model model) {
        setupForm(model);
        return showPage(PAGE_FORM, model);
    }

    @PostMapping(PAGE_SHOW)
    public String showReservationShowPage(@ModelAttribute("reservation") ReservationDto reservation, Model model) {
        // todo add id and token from session
        reservation.setId(172);
        reservation.setToken("zcjWu1WsOY8ywnGfCAIL");
        EmailDto emailDto = reservationService.constructEmail(reservation, PersonalInfoRequestDto.builder().build());
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

    private String showPage(String page, Model model) {
        model.addAttribute("currentpage", page);
        return page;
    }

    private void setupForm(Model model) {
        model.addAttribute("roomsize", "1");
        model.addAttribute("name1", "First Last");
        model.addAttribute("street1", "166 Main Street");
        model.addAttribute("city1", "New York 1F8 4G3");
        model.addAttribute("email1", "test@mailinator.com");
        model.addAttribute("roomtype", "standard");
        model.addAttribute("roomtypes", reservationService.getHotelRoomProperties().toListOfMaps());
    }
}
