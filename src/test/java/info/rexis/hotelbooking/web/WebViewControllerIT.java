package info.rexis.hotelbooking.web;

import info.rexis.hotelbooking.HotelbookingApplication;
import info.rexis.hotelbooking.repositories.regsys.exceptions.RegsysClientError;
import info.rexis.hotelbooking.repositories.regsys.exceptions.RegsysServerError;
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
public class WebViewControllerIT {
    @Autowired
    private WebMockMvcITHelper helper;

    private static final String REQUEST_PATH_THAT_WILL_PUT_INFO_IN_SESSION = "/?id=1&token=lala";
    private static final String REQUEST_PATH_MISSING_PARAMETER = "/?id=2";
    private static final String REQUEST_PATH_FOR_CLIENT_ERROR = "/?id=3&token=" + MockRegsysFeignClientConfig.CLIENTERROR_TOKEN;
    private static final String REQUEST_PATH_FOR_SERVER_ERROR = "/?id=3&token=" + MockRegsysFeignClientConfig.SERVERERROR_TOKEN;
    private static final String REQUEST_PATH_FOR_DENY = "/?id=4&token=" + MockRegsysFeignClientConfig.INVALID_TOKEN;
    private static final String PAGE_MAIN = "/main";
    private static final String PAGE_FORM = "/reservation-form";
    private static final String PAGE_SHOW = "/reservation-show";
    private static final String REQUEST_FOR_REVISIT_TEST = "/?id=2&token=databasetest";

    @Test
    public void shouldReturnMainPage() throws Exception {
        helper.shouldReturnPageWithGet(REQUEST_PATH_THAT_WILL_PUT_INFO_IN_SESSION, "id=\"mainpage\"");
    }

    // MockMvc does not support the global error controller, so test passes when a RegsysServerError has bubbled up
    // anything further would mostly be testing Spring Mvc's error handling
    @Test(expected = RegsysServerError.class)
    public void shouldReturnErrorPageWhenRegsysSaysServerError() throws Exception {
        helper.performGETAndUnwrapNestedException(REQUEST_PATH_FOR_SERVER_ERROR);
    }

    // MockMvc does not support the global error controller, so test passes when a RegsysServerError has bubbled up
    // anything further would mostly be testing Spring Mvc's error handling
    @Test(expected = RegsysClientError.class)
    public void shouldReturnErrorPageWhenRegsysSaysNotOk() throws Exception {
        helper.performGETAndUnwrapNestedException(REQUEST_PATH_FOR_CLIENT_ERROR);
    }

    // MockMvc does not support the global error controller, so test passes when 400 is returned
    @Test
    public void shouldReturnErrorPageWhenMissingParameter() throws Exception {
        helper.shouldReturn4xxWithGet(REQUEST_PATH_MISSING_PARAMETER);
    }

    @Test
    public void shouldReturnForbiddenPageWhenRegsysSaysOkFalse() throws Exception {
        helper.shouldReturnPageWithGet(REQUEST_PATH_FOR_DENY, "id=\"forbidden\"");
    }

    @Test
    public void shouldReturnSessionLostPageIfSessionWithoutInfo() throws Exception {
        helper.shouldReturnPageWithGet(PAGE_MAIN, "id=\"sessionlost\"");
    }

    @Test
    public void shouldReturnMainPageAgainWithoutParamsIfSessionHasInfo() throws Exception {
        helper.obtainSession(REQUEST_PATH_THAT_WILL_PUT_INFO_IN_SESSION);
        helper.shouldReturnPageWithGetUsingSession(PAGE_MAIN, "id=\"mainpage\"");
    }

    @Test
    public void shouldReturnReservationFormPage() throws Exception {
        helper.obtainSession(REQUEST_PATH_THAT_WILL_PUT_INFO_IN_SESSION);
        helper.shouldReturnPageWithGetUsingSession(PAGE_FORM, "id=\"formpage\"");
    }

    @Test
    public void shouldReturnForbiddenIfReservationFormWithoutInfoInSession() throws Exception {
        helper.shouldReturnPageWithGet(PAGE_FORM, "id=\"sessionlost\"");
    }

    @Test
    public void shouldDenyWithoutCsrfToken() throws Exception {
        helper.obtainSession(REQUEST_PATH_THAT_WILL_PUT_INFO_IN_SESSION);
        helper.shouldDenyPostWithoutCsrfToken(PAGE_SHOW);
    }

    @Test
    public void shouldReturnReservationShowPage() throws Exception {
        helper.obtainSession(REQUEST_PATH_THAT_WILL_PUT_INFO_IN_SESSION);
        helper.obtainSessionAndCsrfTokenUsingSession(PAGE_FORM);
        helper.shouldReturnPageWithPost(PAGE_SHOW, "id=\"showpage\"");
    }

    @Test
    public void shouldBringBackEnteredInfoOnSecondVisit() throws Exception {
        helper.obtainSession(REQUEST_FOR_REVISIT_TEST);
        helper.obtainSessionAndCsrfTokenUsingSession(PAGE_FORM);
        helper.shouldReturnPageWithPost(PAGE_SHOW, "id=\"showpage\"", "rewuilgfaf");

        helper.obtainSession(REQUEST_FOR_REVISIT_TEST);
        helper.shouldReturnPageWithGetUsingSession(PAGE_FORM, "value=\"rewuilgfaf\"");
    }
}
