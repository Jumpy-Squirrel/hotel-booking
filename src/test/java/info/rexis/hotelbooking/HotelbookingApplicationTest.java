package info.rexis.hotelbooking;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(JUnit4.class)
@PrepareForTest(SpringApplication.class)
public class HotelbookingApplicationTest {
    @Test
    public void testConfigure() {
        SpringApplicationBuilder application = Mockito.mock(SpringApplicationBuilder.class);
        new HotelbookingApplication().configure(application);
    }

    @Test
    public void testMain() {
        PowerMockito.mockStatic(SpringApplication.class);
        HotelbookingApplication.main(new String[] {"failme"});
    }
}
