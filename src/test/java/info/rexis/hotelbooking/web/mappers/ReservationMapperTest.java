package info.rexis.hotelbooking.web.mappers;

import info.rexis.hotelbooking.services.config.HotelRoomProperties;
import info.rexis.hotelbooking.services.dto.ReservationDto;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.util.Arrays;

public class ReservationMapperTest {
    private class FixedLanguageReservationMapper extends ReservationMapper {
        @Override
        protected String currentLanguage() {
            return "en";
        }
    }

    @Test
    public void mapsCorrectly() {
        ReservationDto input = new ReservationDto(3,
                "name1", "street1", "city1", "country1", "email1",
                "name2", "street2", "city2", "country2", "email2",
                "name3", "street3", "city3", "country3", "email3",
                GERMAN, "20.08.2018", "27.08.2018", 2, "comments", "yes", 17, "token", "the_pk");

        HotelRoomProperties props = new HotelRoomProperties();
        props.setRoomtypes(Arrays.asList(new HotelRoomProperties.RoomTypeInfo(), new HotelRoomProperties.RoomTypeInfo()));
        props.getRoomtypes().get(1).setValue("correctRoomtype");

        Model output = new ExtendedModelMap();
        new FixedLanguageReservationMapper().modelFromReservation(output, input, props);

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

    private static final String GERMAN = "dd.mm.yyyy";
    private static final String ENGLISH = "mm/dd/yyyy";

    @Test
    public void shouldLeaveGermanDateUntouched() {
        dateConversionTest("23.05.1987", GERMAN, GERMAN, "23.05.1987");
    }

    @Test
    public void shouldLeaveEnglishDateUntouched() {
        dateConversionTest("05/23/1987", ENGLISH, ENGLISH, "05/23/1987");
    }

    @Test
    public void shouldConvertToGermanDate() {
        dateConversionTest("12/31/2018", ENGLISH, GERMAN, "31.12.2018");
    }

    @Test
    public void shouldConvertToEnglishDate() {
        dateConversionTest("04.01.2018", GERMAN, ENGLISH, "01/04/2018");
    }

    private void dateConversionTest(String input, String inputformat, String outputformat, String expectedOutput) {
        String output = new ReservationMapper().localeConvert(input, inputformat, outputformat);
        Assertions.assertThat(output).isEqualTo(expectedOutput);
    }
}
