package info.rexis.hotelbooking.web.sessions;

import info.rexis.hotelbooking.services.dto.PersonalInfoDto;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;

@Component
public class PersonalInfoSessionStore {
    public void putPersonalInfoIntoSession(HttpSession session, PersonalInfoDto personalInfo) {
        session.setAttribute("personal", personalInfo);
    }

    public PersonalInfoDto getPersonalInfoFromSession(HttpSession session) {
        PersonalInfoDto personalInfo = (PersonalInfoDto) session.getAttribute("personal");
        if (personalInfo == null) {
            throw new SessionLostClientError("session info not available - either wrong entry point or lost session cookie");
        }
        return personalInfo;
    }
}
