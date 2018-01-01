package info.rexis.hotelbooking.web.mappers;

import info.rexis.hotelbooking.services.config.HotelRoomProperties;
import info.rexis.hotelbooking.services.dto.ProcessStatus;
import info.rexis.hotelbooking.services.dto.ReservationDto;
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
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(JUnit4.class)
@PrepareForTest(LocaleContextHolder.class)
public class ReservationMapperTest {
    private class FixedLanguageReservationMapper extends ReservationMapper {
        private String fixedLanguage;

        FixedLanguageReservationMapper(String fixedLanguage) {
            super(new DateConverter());
            this.fixedLanguage = fixedLanguage;
        }

        @Override
        protected String currentLanguage() {
            return fixedLanguage;
        }
    }

    private ReservationDto constructReservation() {
        return new ReservationDto(3,
                "name1", "street1", "city1", "country1", "email1",
                "name2", "street2", "city2", "country2", "email2",
                "name3", "street3", "city3", "country3", "email3",
                "dd.mm.yyyy", "20.08.2018", "27.08.2018", 2, "comments", "yes",
                17, "token", "the_pk", ProcessStatus.NEW, null);
    }

    private HotelRoomProperties constructHotelRoomProperties() {
        HotelRoomProperties props = new HotelRoomProperties();
        props.setRoomtypes(Arrays.asList(new HotelRoomProperties.RoomTypeInfo(), new HotelRoomProperties.RoomTypeInfo()));
        props.getRoomtypes().get(1).setValue("correctRoomtype");
        return props;
    }

    @Test
    public void mapsToModelCorrectly() {
        ReservationDto input = constructReservation();
        HotelRoomProperties props = constructHotelRoomProperties();

        Model output = new ExtendedModelMap();
        new FixedLanguageReservationMapper("en").modelFromReservation(output, input, props);

        Model expected = new ExtendedModelMap();
        Arrays.stream(new String[] {
                "name1", "street1", "city1", "country1", "email1",
                "name2", "street2", "city2", "country2", "email2",
                "name3", "street3", "city3", "country3", "email3",
                "comments"
        }).forEach(v -> expected.addAttribute(v, v));
        expected.addAttribute("roomsize", "3");
        expected.addAttribute("roomtype", "correctRoomtype");
        expected.addAttribute("arrival", "08/20/2018");
        expected.addAttribute("departure", "08/27/2018");

        Assertions.assertThat(output).isEqualTo(expected);
    }

    @Test
    public void mapsToMapCorrectly() {
        ReservationDto input = constructReservation();
        HotelRoomProperties props = constructHotelRoomProperties();

        Map<String, String> output = new FixedLanguageReservationMapper("de").listviewMapFromReservation(input, props);

        Map<String, String> expected = new HashMap<>();
        expected.put("pk", "the_pk");
        expected.put("name1", "name1");
        expected.put("email1", "email1");
        expected.put("roomsize", "3");
        expected.put("roomtype", "correctRoomtype");
        expected.put("arrival", "20.08.2018");
        expected.put("departure", "27.08.2018");

        Assertions.assertThat(output).isEqualTo(expected);
    }

    @Test
    public void shouldReturnNullWhenInputNull() {
        Assertions.assertThat(new ReservationMapper(null).localeConvert(null, null)).isNull();
    }

    @Test
    public void shouldReturnEmptyWhenInputEmpty() {
        Assertions.assertThat(new ReservationMapper(null).localeConvert("", null)).isEmpty();
    }

    @Test
    public void shouldGetLanguageFromLocale() {
        PowerMockito.mockStatic(LocaleContextHolder.class);
        PowerMockito.when(LocaleContextHolder.getLocale()).thenReturn(Locale.GERMANY);
        Assertions.assertThat(new ReservationMapper(null).currentLanguage()).isEqualTo("de");
    }
}
