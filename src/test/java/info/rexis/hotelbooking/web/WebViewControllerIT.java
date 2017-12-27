package info.rexis.hotelbooking.web;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.servlet.http.HttpSession;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ActiveProfiles({"test"})
@SpringBootTest
@AutoConfigureMockMvc
public class WebViewControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldReturnMainPage() throws Exception {
        shouldReturnPageWithGet("", "id=\"mainpage\"");
    }

    @Test
    public void shouldReturnReservationFormPage() throws Exception {
        shouldReturnPageWithGet(WebViewController.PAGE_FORM, "id=\"formpage\"");
    }

    @Test
    public void shouldDenyWithoutCsrfToken() throws Exception {
        obtainCsrfTokenAndSession(WebViewController.PAGE_FORM);
        shouldDenyPostWithoutCsrfToken(WebViewController.PAGE_SHOW);
    }

    @Test
    public void shouldReturnReservationShowPage() throws Exception {
        obtainCsrfTokenAndSession(WebViewController.PAGE_FORM);
        shouldReturnPageWithPost(WebViewController.PAGE_SHOW, "id=\"showpage\"");
    }

    private void shouldReturnPageWithGet(String requestPath, String expectedExcerpt) throws Exception {
        mockMvc.perform(get("/" + requestPath))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(expectedExcerpt)));
    }

    private String csrftoken;
    private HttpSession session;

    private void obtainCsrfTokenAndSession(String requestPath) throws Exception {
        MvcResult result = mockMvc.perform(get("/" + requestPath))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();

        session = result.getRequest().getSession();

        Pattern p = Pattern.compile(".*name=\"_csrf\" value=\"([a-z0-9-]+)\".*",
                Pattern.MULTILINE | Pattern.DOTALL);
        Matcher m = p.matcher(content);
        if (m.find()) {
            csrftoken = m.group(1);
        } else {
            throw new RuntimeException("Could not obtain a csrf token.");
        }
    }

    private void shouldReturnPageWithPost(String requestPath, String expectedExcerpt) throws Exception {
        mockMvc.perform(
                post("/" + requestPath)
                        .param("_csrf", csrftoken)
                        .session((MockHttpSession) session)
        )
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(expectedExcerpt)));
    }

    private void shouldDenyPostWithoutCsrfToken(String requestPath) throws Exception {
        mockMvc.perform(
                post("/" + WebViewController.PAGE_SHOW)
                        .session((MockHttpSession) session)
        )
                .andExpect(status().isForbidden());
    }
}
