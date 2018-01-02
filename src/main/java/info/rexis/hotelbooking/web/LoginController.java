package info.rexis.hotelbooking.web;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/")
public class LoginController {
    public static final String LOGIN_FORM = "login";
    public static final String LOGOUT = "logout";

    @GetMapping(LOGIN_FORM)
    public String showLoginForm(@RequestParam(name = "error", required = false) String error,
                                HttpSession session,
                                Model model) {
        SecurityContextHolder.getContext().setAuthentication(null);
        if ("true".equals(error)) {
            model.addAttribute("loginerror", "true");
        }
        return LOGIN_FORM;
    }

    @GetMapping(LOGOUT)
    public String logout(HttpSession session, Model model) {
        return showLoginForm("", session, model);
    }
}
