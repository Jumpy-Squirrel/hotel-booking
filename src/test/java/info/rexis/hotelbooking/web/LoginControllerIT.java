package info.rexis.hotelbooking.web;

import info.rexis.hotelbooking.HotelbookingApplication;
import info.rexis.unscanned.MockRegsysFeignClientConfig;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.servlet.http.HttpSession;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ActiveProfiles({"test"})
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = {HotelbookingApplication.class, MockRegsysFeignClientConfig.class})
public class LoginControllerIT {
    @Autowired
    private MockMvc mockMvc;

    private static final String REQUEST_PATH_LOGIN = "/login";
    private static final String REQUEST_PATH_LOGIN_AFTER_ERROR = "/login?error=true";
    private static final String REQUEST_PATH_LOGOUT = "/logout";

    @Test
    public void shouldReturnLoginPage() throws Exception {
        performGETAndExpectOkContaining(REQUEST_PATH_LOGIN, "id=\"loginpage\"", false);
    }

    @Test
    public void shouldReturnLoginPageFirstTime() throws Exception {
        performGETAndExpectOkContaining(REQUEST_PATH_LOGIN, "id=\"firsttimemessage\"", false);
    }

    @Test
    public void shouldReturnLoginPageAfterFailure() throws Exception {
        performGETAndExpectOkContaining(REQUEST_PATH_LOGIN_AFTER_ERROR, "id=\"failuremessage\"", false);
    }

    @Test
    public void shouldLoginUser() throws Exception {
        performGETAndExpectOkContaining(REQUEST_PATH_LOGIN, "id=\"loginpage\"", true);

        performPOSTAndExpectRedirectToUrl(REQUEST_PATH_LOGIN, "/hotel/hotel-list");
    }

    @Test
    public void shouldLogoutUserAndShowLoginFormAgain() throws Exception {
        shouldLoginUser();

        performGETAndExpectRedirectToUrlContaining(REQUEST_PATH_LOGOUT, "/login");
    }

    private String csrftoken;
    private HttpSession session;

    private void performGETAndExpectOkContaining(String requestPath, String expectedExcerpt, boolean extractSessionAndCsrfToken) throws Exception {
        MvcResult result = mockMvc.perform(get(requestPath).header("Accept", "text/html"))
                .andExpect(status().isOk())
                .andExpect(content().string(CoreMatchers.containsString(expectedExcerpt)))
                .andReturn();

        if (extractSessionAndCsrfToken) {
            // need to do this because the csrf token also changes the session
            session = result.getRequest().getSession();

            String content = result.getResponse().getContentAsString();

            Pattern p = Pattern.compile(".*name=\"_csrf\" value=\"([a-z0-9-]+)\".*",
                    Pattern.MULTILINE | Pattern.DOTALL);
            Matcher m = p.matcher(content);
            if (m.find()) {
                csrftoken = m.group(1);
            } else {
                throw new RuntimeException("Could not obtain a csrf token.");
            }
        }
    }

    private void performPOSTAndExpectRedirectToUrl(String requestPath, String expectedRedirectUrl) throws Exception {
        mockMvc.perform(
                post(requestPath)
                        .param("_csrf", csrftoken)
                        .param("username", "user")
                        .param("password", "demoPw")
                        .session((MockHttpSession) session)
        )
                .andExpect(status().isFound())
                .andExpect(redirectedUrl(expectedRedirectUrl));
    }

    private void performGETAndExpectRedirectToUrlContaining(String requestPath, String expectedRedirectUrlPart) throws Exception {
        mockMvc.perform(get(requestPath).header("Accept", "text/html"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", CoreMatchers.containsString(expectedRedirectUrlPart)));
    }
}
