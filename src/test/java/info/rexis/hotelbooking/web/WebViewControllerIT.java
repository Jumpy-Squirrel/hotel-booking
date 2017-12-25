package info.rexis.hotelbooking.web;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
        shouldReturnPageWith("", "This is some text.");
    }

    @Test
    public void shouldReturnReservationFormPage() throws Exception {
        shouldReturnPageWith(WebViewController.PAGE_FORM, "booking request on your behalf");
    }

    @Test
    public void shouldReturnReservationShowPage() throws Exception {
        shouldReturnPageWith(WebViewController.PAGE_SHOW, "This shows the sent reservation.");
    }

    private void shouldReturnPageWith(String requestPath, String expectedExcerpt) throws Exception {
        mockMvc.perform(get("/" + requestPath))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(expectedExcerpt)));
    }
}
