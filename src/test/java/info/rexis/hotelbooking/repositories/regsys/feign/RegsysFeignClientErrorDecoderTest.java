package info.rexis.hotelbooking.repositories.regsys.feign;

import feign.FeignException;
import feign.Response;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.util.Collections;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(JUnit4.class)
@PrepareForTest(FeignException.class)
public class RegsysFeignClientErrorDecoderTest {
    @Test
    public void shouldFallthroughToFeignExceptionHandler1() {
        shouldFallthroughToFeignExceptionHandlerWithStatus(300);
    }

    @Test
    public void shouldFallthroughToFeignExceptionHandler2() {
        shouldFallthroughToFeignExceptionHandlerWithStatus(600);
    }

    private void shouldFallthroughToFeignExceptionHandlerWithStatus(int httpStatus) {
        Response response = Response.builder().status(httpStatus).headers(Collections.emptyMap()).build();

        PowerMockito.mockStatic(FeignException.class);
        PowerMockito.when(FeignException.errorStatus("me", response)).thenReturn(null);

        RegsysFeignClientErrorDecoder cut = new RegsysFeignClientErrorDecoder();
        Assertions.assertThat(cut.decode("me", response)).isNull();
    }
}
