package info.rexis.hotelbooking.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebViewController {
    @GetMapping("/")
    public String showMainPage() {
        return "main";
    }
}
