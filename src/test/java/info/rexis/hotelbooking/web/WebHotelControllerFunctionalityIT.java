package info.rexis.hotelbooking.web;

import info.rexis.hotelbooking.HotelbookingApplication;
import info.rexis.hotelbooking.repositories.database.DatabaseRepository;
import info.rexis.hotelbooking.services.dto.ProcessStatus;
import info.rexis.hotelbooking.services.dto.ReservationDto;
import info.rexis.unscanned.MockRegsysFeignClientConfig;
import org.assertj.core.api.Assertions;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
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
public class WebHotelControllerFunctionalityIT {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DatabaseRepository database;

    private static final String REQUEST_PATH_LIST = "/hotel/hotel-list";
    private static final String REQUEST_PATH_FORM = "/hotel/hotel-form";
    private static final String REQUEST_PATH_DONE = "/hotel/hotel-done";

    @Test
    public void shouldReturnListPage() throws Exception {
        logInUser();

        performGETAndExpectOkContaining(REQUEST_PATH_LIST, "id=\"hotellist\"", false);
    }

    @Test
    public void shouldReturnFormPage() throws Exception {
        ReservationDto reservation = new ReservationDto();
        reservation.setStatus(ProcessStatus.NEW);
        database.saveReservation(reservation);
        logInUser();

        performGETAndExpectOkContaining(REQUEST_PATH_FORM + "?pk=" + reservation.getPk(), "id=\"hotelform\"", false);
    }

    @Test
    public void shouldResetStatusAndReturnListPageAgain() throws Exception {
        ReservationDto reservation = new ReservationDto();
        reservation.setStatus(ProcessStatus.PROCESSING);
        database.saveReservation(reservation);
        logInUser();

        performGETAndExpectOkContaining(REQUEST_PATH_DONE + "?pk=" + reservation.getPk(), "id=\"hotellist\"", false);
        reservation = database.loadReservationOrThrow(reservation.getPk());

        Assertions.assertThat(reservation.getStatus()).isEqualTo(ProcessStatus.NEW);
    }

    @Test
    public void shouldSetFinalStatusAndReturnListPageAgain() throws Exception {
        ReservationDto reservation = new ReservationDto();
        reservation.setStatus(ProcessStatus.PROCESSING);
        database.saveReservation(reservation);
        logInUser();

        // get the csrf token + session
        performGETAndExpectOkContaining(REQUEST_PATH_FORM + "?pk=" + reservation.getPk(), "id=\"hotelform\"", true);

        performPOSTAndExpectOkContaining(REQUEST_PATH_DONE, "id=\"hotellist\"", reservation.getPk());
        reservation = database.loadReservationOrThrow(reservation.getPk());

        Assertions.assertThat(reservation.getStatus()).isEqualTo(ProcessStatus.DONE);
    }

    private void logInUser() {
        List<GrantedAuthority> list = new ArrayList<>();
        list.add(new SimpleGrantedAuthority("ROLE_USER"));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("user", "demoPw", list);
        SecurityContextHolder.getContext().setAuthentication(auth);
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

    private void performPOSTAndExpectOkContaining(String requestPath, String expectedExcerpt, String pk) throws Exception {
        mockMvc.perform(
                post(requestPath)
                        .param("_csrf", csrftoken)
                        .param("pk", pk)
                        .session((MockHttpSession) session)
        )
                .andExpect(status().isOk())
                .andExpect(content().string(CoreMatchers.containsString(expectedExcerpt)));
    }
}
