package info.rexis.hotelbooking.web;

import info.rexis.hotelbooking.HotelbookingApplication;
import info.rexis.unscanned.MockRegsysFeignClientConfig;
import info.rexis.unscanned.WebMockMvcITHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ActiveProfiles({"test"})
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = {HotelbookingApplication.class, MockRegsysFeignClientConfig.class, WebMockMvcITHelper.class})
public class LoginControllerIT {
    @Autowired
    private WebMockMvcITHelper helper;

    private static final String REQUEST_PATH_LOGIN = "/login";
    private static final String REQUEST_PATH_LOGIN_AFTER_ERROR = "/login?error=true";
    private static final String REQUEST_PATH_LOGOUT = "/logout";

    @Test
    public void shouldReturnLoginPage() throws Exception {
        helper.performGETAndExpectOkContaining(REQUEST_PATH_LOGIN, "id=\"loginpage\"", false);
    }

    @Test
    public void shouldReturnLoginPageFirstTime() throws Exception {
        helper.performGETAndExpectOkContaining(REQUEST_PATH_LOGIN, "id=\"firsttimemessage\"", false);
    }

    @Test
    public void shouldReturnLoginPageAfterFailure() throws Exception {
        helper.performGETAndExpectOkContaining(REQUEST_PATH_LOGIN_AFTER_ERROR, "id=\"failuremessage\"", false);
    }

    @Test
    public void shouldLoginUser() throws Exception {
        helper.performGETAndExpectOkContaining(REQUEST_PATH_LOGIN, "id=\"loginpage\"", true);

        helper.performPOSTAndExpectRedirectToUrl(REQUEST_PATH_LOGIN, "/hotel/hotel-list");
    }

    @Test
    public void shouldLogoutUserAndShowLoginFormAgain() throws Exception {
        shouldLoginUser();

        helper.performGETAndExpectOkContaining(REQUEST_PATH_LOGOUT, "id=\"loginpage\"", false);
    }
}
