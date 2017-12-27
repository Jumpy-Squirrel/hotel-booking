package info.rexis.hotelbooking.web;

import info.rexis.hotelbooking.services.ReservationService;
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
        String email = reservationService.constructEmail(reservation, PersonalInfoRequestDto.builder().build());
        model.addAttribute("email", email);
        return showPage(PAGE_SHOW, model);
    }

    private String showPage(String page, Model model) {
        model.addAttribute("currentpage", page);
        return page;
    }

    private void setupForm(Model model) {
        model.addAttribute("roomsize", "1");
        model.addAttribute("name1", "First Last");
        model.addAttribute("name2", "Second Name");
        model.addAttribute("name3", "Third Person");
        model.addAttribute("street1", "166 Main Street");
        model.addAttribute("street2", "2 Central Ave");
        model.addAttribute("street3", "4 Hopswitch Drive");
        model.addAttribute("city1", "New York 1F8 4G3");
        model.addAttribute("city2", "Atlanta 2AB 3CF");
        model.addAttribute("city3", "Minicity 9QF 3RR");
        model.addAttribute("email1", "test@mailinator.com");
        model.addAttribute("email2", "test2@mailinator.com");
        model.addAttribute("email3", "test3@mailinator.com");
        model.addAttribute("roomtype", "standard");
        model.addAttribute("roomtypes", reservationService.getHotelRoomProperties().toListOfMaps());
    }
}
