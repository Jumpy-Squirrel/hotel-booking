package info.rexis.hotelbooking.web;

import info.rexis.hotelbooking.HotelbookingApplication;
import info.rexis.hotelbooking.repositories.database.DatabaseRepository;
import info.rexis.hotelbooking.services.dto.ProcessStatus;
import info.rexis.hotelbooking.services.dto.ReservationDto;
import info.rexis.unscanned.MockRegsysFeignClientConfig;
import info.rexis.unscanned.WebMockMvcITHelper;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@ActiveProfiles({"test"})
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = {HotelbookingApplication.class, MockRegsysFeignClientConfig.class, WebMockMvcITHelper.class})
public class WebHotelControllerFunctionalityIT {
    @Autowired
    private WebMockMvcITHelper helper;

    @Autowired
    private DatabaseRepository database;

    private static final String REQUEST_PATH_LIST = "/hotel/hotel-list";
    private static final String REQUEST_PATH_FORM = "/hotel/hotel-form";
    private static final String REQUEST_PATH_DONE = "/hotel/hotel-done";

    @Test
    public void shouldReturnListPage() throws Exception {
        logInUser();

        helper.performGETAndExpectOkContaining(REQUEST_PATH_LIST, "id=\"hotellist\"", false);
    }

    @Test
    public void shouldReturnFormPage() throws Exception {
        ReservationDto reservation = new ReservationDto();
        reservation.setStatus(ProcessStatus.NEW);
        database.saveReservation(reservation);
        logInUser();

        helper.performGETAndExpectOkContaining(REQUEST_PATH_FORM + "?pk=" + reservation.getPk(), "id=\"hotelform\"", false);
    }

    @Test
    public void shouldResetStatusAndReturnListPageAgain() throws Exception {
        ReservationDto reservation = new ReservationDto();
        reservation.setStatus(ProcessStatus.PROCESSING);
        database.saveReservation(reservation);
        logInUser();

        helper.performGETAndExpectOkContaining(REQUEST_PATH_DONE + "?pk=" + reservation.getPk(), "id=\"hotellist\"", false);
        reservation = database.loadReservationOrThrow(reservation.getPk());

        Assertions.assertThat(reservation.getStatus()).isEqualTo(ProcessStatus.NEW);
    }

    @Test
    public void shouldSetFinalStatusAndReturnListPageAgain() throws Exception {
        ReservationDto reservation = new ReservationDto();
        reservation.setStatus(ProcessStatus.NEW);
        database.saveReservation(reservation);
        logInUser();

        // get the csrf token + session
        helper.performGETAndExpectOkContaining(REQUEST_PATH_FORM + "?pk=" + reservation.getPk(), "id=\"hotelform\"", true);

        helper.performPOSTAndExpectOkContaining(REQUEST_PATH_DONE, "id=\"hotellist\"", reservation.getPk());
        reservation = database.loadReservationOrThrow(reservation.getPk());

        Assertions.assertThat(reservation.getStatus()).isEqualTo(ProcessStatus.DONE);
    }

    @Test
    public void shouldRejectLockingAndShowListPageWithWarning() throws Exception {
        ReservationDto reservation = new ReservationDto();
        reservation.setStatus(ProcessStatus.PROCESSING);
        reservation.setSession("someone_elses_session");
        database.saveReservation(reservation);
        logInUser();

        // get the csrf token + session
        helper.performGETAndExpectOkContaining(REQUEST_PATH_FORM + "?pk=" + reservation.getPk(), "Dieser Eintrag wird oder wurde bereits von einem anderen Benutzer bearbeitet", false);

        reservation = database.loadReservationOrThrow(reservation.getPk());
        Assertions.assertThat(reservation.getSession()).isEqualTo("someone_elses_session");
    }

    private void logInUser() {
        List<GrantedAuthority> list = new ArrayList<>();
        list.add(new SimpleGrantedAuthority("ROLE_USER"));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("user", "demoPw", list);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
