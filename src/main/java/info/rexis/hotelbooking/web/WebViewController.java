package info.rexis.hotelbooking.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class WebViewController {
    public static final String PAGE_MAIN = "main";
    public static final String PAGE_FORM = "reservation-form";
    public static final String PAGE_SHOW = "reservation-show";

    @GetMapping
    public String showMainPage(Model model) {
        return showPage(PAGE_MAIN, model);
    }

    @GetMapping(PAGE_FORM)
    public String showReservationFormPage(Model model) {
        setupForm(model);
        return showPage(PAGE_FORM, model);
    }

    @GetMapping(PAGE_SHOW)
    public String showReservationShowPage(Model model) {
        return showPage(PAGE_SHOW, model);
    }

    private String showPage(String page, Model model) {
        model.addAttribute("currentpage", page);
        return page;
    }

    private void setupForm(Model model) {
        model.addAttribute("roomsize", "1");
        model.addAttribute("nick1", "Nickname1");
        model.addAttribute("nick2", "");
        model.addAttribute("nick3", "");
        model.addAttribute("email1", "test@mailinator.com");
        model.addAttribute("email2", "");
        model.addAttribute("email3", "");
    }
}
