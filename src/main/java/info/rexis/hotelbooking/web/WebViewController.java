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
        model.addAttribute("name2", "");
        model.addAttribute("name3", "");
        model.addAttribute("street1", "166 Main Street");
        model.addAttribute("street2", "");
        model.addAttribute("street3", "");
        model.addAttribute("city1", "New York 1F8 4G3");
        model.addAttribute("city2", "");
        model.addAttribute("city3", "");
        model.addAttribute("email1", "test@mailinator.com");
        model.addAttribute("email2", "");
        model.addAttribute("email3", "");
        model.addAttribute("roomtype", "stadium");
        model.addAttribute("roomtypes", reservationService.getHotelRoomProperties().toListOfMaps());
    }
}
