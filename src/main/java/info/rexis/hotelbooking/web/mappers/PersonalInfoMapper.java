package info.rexis.hotelbooking.web.mappers;

import info.rexis.hotelbooking.services.dto.PersonalInfoDto;
import org.springframework.ui.Model;

public class PersonalInfoMapper {
    public static void modelFromPersonalInfo(Model model, PersonalInfoDto info) {
        model.addAttribute("name1", info.getName());
        model.addAttribute("street1", info.getStreet());
        model.addAttribute("city1", info.getCity());
        model.addAttribute("country1", info.getCountry());
        model.addAttribute("email1", info.getEmail());
    }
}
