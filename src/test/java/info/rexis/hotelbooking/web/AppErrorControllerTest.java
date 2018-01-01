package info.rexis.hotelbooking.web;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.http.HttpServletRequest;

public class AppErrorControllerTest {
    @Test
    public void testGetErrorPath() {
        Assertions.assertThat(new AppErrorController(null, null).getErrorPath())
                .isEqualTo("/error");
    }

    @Test
    public void shouldReturnInternalServerErrorForInvalidStatus() {
        HttpServletRequest invalidStatusRequest = new MockHttpServletRequest();
        invalidStatusRequest.setAttribute("javax.servlet.error.status_code", 17438);
        Assertions.assertThat(new AppErrorController(null, null).getStatus(invalidStatusRequest))
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    public void shouldReturnInternalServerErrorForNullStatus() {
        HttpServletRequest nullStatusRequest = new MockHttpServletRequest();
        Assertions.assertThat(new AppErrorController(null, null).getStatus(nullStatusRequest))
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
