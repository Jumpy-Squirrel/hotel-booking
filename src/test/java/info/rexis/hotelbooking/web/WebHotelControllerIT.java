package info.rexis.hotelbooking.web;

import info.rexis.hotelbooking.HotelbookingApplication;
import info.rexis.unscanned.MockRegsysFeignClientConfig;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test"})
@ContextConfiguration(classes = {HotelbookingApplication.class, MockRegsysFeignClientConfig.class})
public class WebHotelControllerIT {
    @Autowired
    private TestRestTemplate template;

    @Test
    public void shouldProtectHotelList() {
        shouldRedirectToLogin("/hotel/hotel-list", "text/html", HttpMethod.GET);
    }

    @Test
    public void shouldProtectHotelListJson() {
        shouldRedirectToLogin("/hotel/hotel-list", "application/json", HttpMethod.GET);
    }

    @Test
    public void shouldProtectHotelForm() {
        shouldRedirectToLogin("/hotel/hotel-form", "text/html", HttpMethod.GET);
    }

    @Test
    public void shouldProtectHotelDone() {
        shouldRedirectToLogin("/hotel/hotel-done", "text/html", HttpMethod.GET);
    }

    @Test
    public void shouldProtectHotelDonePost() {
        shouldRedirectToLogin("/hotel/hotel-done", "text/html", HttpMethod.POST);
    }

    private void shouldRedirectToLogin(String url, String acceptheader, HttpMethod method) {
        ResponseEntity<String> response = template.exchange(url, method, new HttpEntity<>(headers(acceptheader)), String.class);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        Assertions.assertThat(response.getHeaders().getLocation().getPath()).contains("/login");
    }

    private HttpHeaders headers(String accept) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", accept);
        return headers;
    }
}
