package info.rexis.hotelbooking.web;

import info.rexis.hotelbooking.HotelbookingApplication;
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
public class AppErrorControllerIT {
    @Autowired
    private TestRestTemplate template;

    @Test
    public void shouldReturnHtmlErrorPageForMissingIdParameter() {
        ResponseEntity<String> response = template.exchange("/?token=bla", HttpMethod.GET, new HttpEntity<>(headers("text/html")), String.class);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertThat(response.getBody()).contains("id=\"errorpage\"");
    }

    @Test
    public void shouldReturnJsonErrorPageForMissingIdParameter() {
        ResponseEntity<String> response = template.exchange("/?token=bla", HttpMethod.GET, new HttpEntity<>(headers("application/json")), String.class);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertThat(response.getBody()).contains("\"message\":\"No valid id provided or no token provided\"");
    }

    @Test
    public void shouldReturnHtmlErrorPageForMissingTokenParameter() {
        ResponseEntity<String> response = template.exchange("/?id=13", HttpMethod.GET, new HttpEntity<>(headers("text/html")), String.class);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertThat(response.getBody()).contains("id=\"errorpage\"");
    }

    @Test
    public void shouldReturnJsonErrorPageForMissingTokenParameter() {
        ResponseEntity<String> response = template.exchange("/?id=13", HttpMethod.GET, new HttpEntity<>(headers("application/json")), String.class);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertThat(response.getBody()).contains("\"message\":\"No valid id provided or no token provided\"");
    }

    private HttpHeaders headers(String accept) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", accept);
        return headers;
    }
}
