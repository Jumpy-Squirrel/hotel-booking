package info.rexis.hotelbooking.repositories.regsys.feign;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import javax.net.ssl.SSLContext;
import java.security.NoSuchAlgorithmException;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(JUnit4.class)
@PrepareForTest(SSLContext.class)
public class RegsysFeignConfigurationTest {
    @Test
    public void testTrustEverythingTrustManagerGetAcceptedIssuers() {
        Assertions.assertThat(new RegsysFeignConfiguration.TrustEverythingTrustManager().getAcceptedIssuers())
                .isNull();
    }

    @Test
    public void testTrustEverythingTrustManagerCheckClientTrusted() {
        new RegsysFeignConfiguration.TrustEverythingTrustManager().checkClientTrusted(null, null);
    }

    @Test
    public void testTrustEverythingTrustManagerCheckServerTrusted() {
        new RegsysFeignConfiguration.TrustEverythingTrustManager().checkServerTrusted(null, null);
    }

    @Test(expected = RuntimeException.class)
    public void testExceptionHandlingInsideTrustEverythingSSLContext() throws Exception {
        PowerMockito.mockStatic(SSLContext.class);
        PowerMockito.when(SSLContext.getInstance("SSL")).thenThrow(new NoSuchAlgorithmException());
        new RegsysFeignConfiguration().feignClient();
    }
}
