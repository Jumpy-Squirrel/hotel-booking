package info.rexis.hotelbooking;

import info.rexis.hotelbooking.web.WebViewController;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles({"test"})
public class HotelbookingApplicationIT {
    @Autowired
    private WebViewController webViewController;

    @Test
    public void contextLoads() {
        Assertions.assertThat(webViewController).isNotNull();
    }
}
