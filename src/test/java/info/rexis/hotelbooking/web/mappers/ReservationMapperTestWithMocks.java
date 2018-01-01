package info.rexis.hotelbooking.web.mappers;

import info.rexis.hotelbooking.util.mappers.DateConverter;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(JUnit4.class)
@PrepareForTest(LocaleContextHolder.class)
public class ReservationMapperTestWithMocks {
    @Test
    public void shouldGetLanguageFromLocale() {
        PowerMockito.mockStatic(LocaleContextHolder.class);
        PowerMockito.when(LocaleContextHolder.getLocale()).thenReturn(Locale.GERMANY);
        Assertions.assertThat(new ReservationMapper(new DateConverter()).currentLanguage()).isEqualTo("de");
    }
}
