package info.rexis.hotelbooking.web;

import info.rexis.hotelbooking.HotelbookingApplication;
import info.rexis.hotelbooking.repositories.regsys.exceptions.RegsysClientError;
import info.rexis.hotelbooking.repositories.regsys.exceptions.RegsysServerError;
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
import org.springframework.web.util.NestedServletException;

import javax.servlet.http.HttpSession;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ActiveProfiles({"test"})
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = {HotelbookingApplication.class, MockRegsysFeignClientConfig.class})
public class WebViewControllerIT {
    @Autowired
    private MockMvc mockMvc;

    private static final String REQUEST_PATH_THAT_WILL_PUT_INFO_IN_SESSION = "?id=1&token=lala";
    private static final String REQUEST_PATH_MISSING_PARAMETER = "?id=2";
    private static final String REQUEST_PATH_FOR_CLIENT_ERROR = "?id=3&token=" + MockRegsysFeignClientConfig.CLIENTERROR_TOKEN;
    private static final String REQUEST_PATH_FOR_SERVER_ERROR = "?id=3&token=" + MockRegsysFeignClientConfig.SERVERERROR_TOKEN;
    private static final String REQUEST_PATH_FOR_DENY = "?id=4&token=" + MockRegsysFeignClientConfig.INVALID_TOKEN;

    @Test
    public void shouldReturnMainPage() throws Exception {
        shouldReturnPageWithGet(REQUEST_PATH_THAT_WILL_PUT_INFO_IN_SESSION, "id=\"mainpage\"");
    }

    // MockMvc does not support the global error controller, so test passes when a RegsysServerError has bubbled up
    // anything further would mostly be testing Spring Mvc's error handling
    @Test(expected = RegsysServerError.class)
    public void shouldReturnErrorPageWhenRegsysSaysServerError() throws Exception {
        try {
            mockMvc.perform(get("/" + REQUEST_PATH_FOR_SERVER_ERROR).header("Accept", "text/html"));
        } catch (NestedServletException nested) {
            throw (Exception) nested.getCause();
        }
    }

    // MockMvc does not support the global error controller, so test passes when a RegsysServerError has bubbled up
    // anything further would mostly be testing Spring Mvc's error handling
    @Test(expected = RegsysClientError.class)
    public void shouldReturnErrorPageWhenRegsysSaysNotOk() throws Exception {
        try {
            mockMvc.perform(get("/" + REQUEST_PATH_FOR_CLIENT_ERROR).header("Accept", "text/html"));
        } catch (NestedServletException nested) {
            throw (Exception) nested.getCause();
        }
    }

    // MockMvc does not support the global error controller, so test passes when 400 is returned
    @Test
    public void shouldReturnErrorPageWhenMissingParameter() throws Exception {
        shouldReturn4xxWithGet(REQUEST_PATH_MISSING_PARAMETER);
    }

    @Test
    public void shouldReturnForbiddenPageWhenRegsysSaysOkFalse() throws Exception {
        shouldReturnPageWithGet(REQUEST_PATH_FOR_DENY, "id=\"forbidden\"");
    }

    @Test
    public void shouldReturnSessionLostPageIfSessionWithoutInfo() throws Exception {
        shouldReturnPageWithGet(WebViewController.PAGE_MAIN, "id=\"sessionlost\"");
    }

    @Test
    public void shouldReturnMainPageAgainWithoutParamsIfSessionHasInfo() throws Exception {
        obtainSession(REQUEST_PATH_THAT_WILL_PUT_INFO_IN_SESSION);
        shouldReturnPageWithGet(WebViewController.PAGE_MAIN, "id=\"mainpage\"", session);
    }

    @Test
    public void shouldReturnReservationFormPage() throws Exception {
        obtainSession(REQUEST_PATH_THAT_WILL_PUT_INFO_IN_SESSION);
        shouldReturnPageWithGet(WebViewController.PAGE_FORM, "id=\"formpage\"", session);
    }

    @Test
    public void shouldReturnForbiddenIfReservationFormWithoutInfoInSession() throws Exception {
        shouldReturnPageWithGet(WebViewController.PAGE_FORM, "id=\"sessionlost\"");
    }

    @Test
    public void shouldDenyWithoutCsrfToken() throws Exception {
        obtainSession(REQUEST_PATH_THAT_WILL_PUT_INFO_IN_SESSION);
        shouldDenyPostWithoutCsrfToken(WebViewController.PAGE_SHOW);
    }

    @Test
    public void shouldReturnReservationShowPage() throws Exception {
        obtainSession(REQUEST_PATH_THAT_WILL_PUT_INFO_IN_SESSION);
        obtainSessionAndCsrfToken(WebViewController.PAGE_FORM, session);
        shouldReturnPageWithPost(WebViewController.PAGE_SHOW, "id=\"showpage\"");
    }

    @Test
    public void shouldBringBackEnteredInfoOnSecondVisit() throws Exception {
        obtainSession("?id=2&token=databasetest");
        obtainSessionAndCsrfToken(WebViewController.PAGE_FORM, session);
        shouldReturnPageWithPost(WebViewController.PAGE_SHOW, "id=\"showpage\"", "rewuilgfaf");

        obtainSession("?id=2&token=databasetest");
        shouldReturnPageWithGet(WebViewController.PAGE_FORM, "value=\"rewuilgfaf\"", session);
    }

    private String csrftoken;
    private HttpSession session;

    private void obtainSession(String requestPath) throws Exception {
        MvcResult result = mockMvc.perform(get("/" + requestPath))
                .andExpect(status().isOk())
                .andReturn();
        session = result.getRequest().getSession();
    }

    private void obtainSessionAndCsrfToken(String requestPath, HttpSession httpSession) throws Exception {
        MvcResult result = mockMvc.perform(get("/" + requestPath).session((MockHttpSession) httpSession))
                .andExpect(status().isOk())
                .andReturn();
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

    private void shouldReturnPageWithGet(String requestPath, String expectedExcerpt) throws Exception {
        mockMvc.perform(get("/" + requestPath).header("Accept", "text/html"))
                .andExpect(status().isOk())
                .andExpect(content().string(CoreMatchers.containsString(expectedExcerpt)));
    }

    private void shouldReturn4xxWithGet(String requestPath) throws Exception {
        mockMvc.perform(get("/" + requestPath).header("Accept", "text/html"))
                .andExpect(status().is4xxClientError());
    }

    private void shouldReturnPageWithGet(String requestPath, String expectedExcerpt, HttpSession httpSession) throws Exception {
        mockMvc.perform(get("/" + requestPath).session((MockHttpSession) httpSession).header("Accept", "text/html"))
                .andExpect(status().isOk())
                .andExpect(content().string(CoreMatchers.containsString(expectedExcerpt)));
    }

    private void shouldReturnPageWithPost(String requestPath, String expectedExcerpt, String name3value) throws Exception {
        mockMvc.perform(
                post("/" + requestPath)
                        .param("_csrf", csrftoken)
                        .param("roomtype", "1")
                        .param("name3", name3value)
                        .session((MockHttpSession) session)
        )
                .andExpect(status().isOk())
                .andExpect(content().string(CoreMatchers.containsString(expectedExcerpt)));
    }

    private void shouldReturnPageWithPost(String requestPath, String expectedExcerpt) throws Exception {
        shouldReturnPageWithPost(requestPath, expectedExcerpt, "");
    }

    private void shouldDenyPostWithoutCsrfToken(String requestPath) throws Exception {
        mockMvc.perform(
                post("/" + WebViewController.PAGE_SHOW)
                        .session((MockHttpSession) session)
        )
                .andExpect(status().isForbidden());
    }
}
