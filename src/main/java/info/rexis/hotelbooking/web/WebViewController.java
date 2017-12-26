package info.rexis.hotelbooking.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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

    private Map<String, String> roominfo(String value, String name) {
        Map<String, String> info = new HashMap<>();
        info.put("name", name);
        info.put("value", value);
        info.put("description", "description for some room type");
        info.put("infolink", "http://localhost/something");
        info.put("price1", "$84.00");
        info.put("price2", "$99.00");
        info.put("price3", "$104.00");
        return info;
    }

    private void setupForm(Model model) {
        model.addAttribute("roomsize", "1");
        model.addAttribute("nick1", "Nickname1");
        model.addAttribute("nick2", "");
        model.addAttribute("nick3", "");
        model.addAttribute("name1", "First Last");
        model.addAttribute("name2", "");
        model.addAttribute("name3", "");
        model.addAttribute("street1", "166 Main Street");
        model.addAttribute("street2", "");
        model.addAttribute("street3", "");
        model.addAttribute("city1", "New York 1F8 4G3");
        model.addAttribute("city2", "");
        model.addAttribute("city3", "");
        model.addAttribute("phone1", "+555 4444 123");
        model.addAttribute("phone2", "");
        model.addAttribute("phone3", "");
        model.addAttribute("email1", "test@mailinator.com");
        model.addAttribute("email2", "");
        model.addAttribute("email3", "");
        model.addAttribute("roomtype", "stadium");
        model.addAttribute("roomtypes", Arrays.asList(
                roominfo("hall", "Concert Hall"),
                roominfo("stadium", "Stadium"),
                roominfo("shack", "Shack in the Wilderness")
        ));
    }
}
