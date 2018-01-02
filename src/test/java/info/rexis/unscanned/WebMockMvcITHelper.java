package info.rexis.unscanned;

import org.hamcrest.CoreMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.util.NestedServletException;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Component
public class WebMockMvcITHelper {
    @Autowired
    private MockMvc mockMvc;

    private String csrftoken;
    private HttpSession session;

    public void performGETAndExpectOkContaining(String requestPath, String expectedExcerpt, boolean extractSessionAndCsrfToken) throws Exception {
        MvcResult result = mockMvc.perform(get(requestPath).header("Accept", "text/html"))
                .andExpect(status().isOk())
                .andExpect(content().string(CoreMatchers.containsString(expectedExcerpt)))
                .andReturn();

        if (extractSessionAndCsrfToken) {
            extractSessionAndCsrfToken(result);
        }
    }

    public void performPOSTAndExpectOkContaining(String requestPath, String expectedExcerpt, String pk) throws Exception {
        mockMvc.perform(
                post(requestPath)
                        .param("_csrf", csrftoken)
                        .param("pk", pk)
                        .session((MockHttpSession) session)
        )
                .andExpect(status().isOk())
                .andExpect(content().string(CoreMatchers.containsString(expectedExcerpt)));
    }

    public void performGETAndExpectRedirectToUrlContaining(String requestPath, String expectedRedirectUrlPart) throws Exception {
        mockMvc.perform(get(requestPath).header("Accept", "text/html"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", CoreMatchers.containsString(expectedRedirectUrlPart)));
    }

    public void performPOSTAndExpectRedirectToUrl(String requestPath, String expectedRedirectUrl) throws Exception {
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

    public void obtainSession(String requestPath) throws Exception {
        MvcResult result = mockMvc.perform(get(requestPath))
                .andExpect(status().isOk())
                .andReturn();
        session = result.getRequest().getSession();
    }

    public void obtainSessionAndCsrfTokenUsingSession(String requestPath) throws Exception {
        MvcResult result = mockMvc.perform(get(requestPath).session((MockHttpSession) session))
                .andExpect(status().isOk())
                .andReturn();
        extractSessionAndCsrfToken(result);
    }

    public void shouldReturnPageWithGet(String requestPath, String expectedExcerpt) throws Exception {
        mockMvc.perform(get(requestPath).header("Accept", "text/html"))
                .andExpect(status().isOk())
                .andExpect(content().string(CoreMatchers.containsString(expectedExcerpt)));
    }

    public void shouldReturn4xxWithGet(String requestPath) throws Exception {
        mockMvc.perform(get(requestPath).header("Accept", "text/html"))
                .andExpect(status().is4xxClientError());
    }

    public void shouldReturnPageWithGetUsingSession(String requestPath, String expectedExcerpt) throws Exception {
        mockMvc.perform(get(requestPath).session((MockHttpSession) session).header("Accept", "text/html"))
                .andExpect(status().isOk())
                .andExpect(content().string(CoreMatchers.containsString(expectedExcerpt)));
    }

    public void shouldReturnPageWithPost(String requestPath, String expectedExcerpt, String name3value) throws Exception {
        mockMvc.perform(
                post(requestPath)
                        .param("_csrf", csrftoken)
                        .param("roomtype", "1")
                        .param("name3", name3value)
                        .session((MockHttpSession) session)
        )
                .andExpect(status().isOk())
                .andExpect(content().string(CoreMatchers.containsString(expectedExcerpt)));
    }

    public void shouldReturnPageWithPost(String requestPath, String expectedExcerpt) throws Exception {
        shouldReturnPageWithPost(requestPath, expectedExcerpt, "");
    }

    public void shouldDenyPostWithoutCsrfToken(String requestPath) throws Exception {
        mockMvc.perform(
                post(requestPath)
                        .session((MockHttpSession) session)
        )
                .andExpect(status().isForbidden());
    }

    public void performGETAndUnwrapNestedException(String requestPath) throws Exception {
        try {
            mockMvc.perform(get(requestPath).header("Accept", "text/html"));
        } catch (NestedServletException nested) {
            throw (Exception) nested.getCause();
        }
    }

    private void extractSessionAndCsrfToken(MvcResult result) throws UnsupportedEncodingException {
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
