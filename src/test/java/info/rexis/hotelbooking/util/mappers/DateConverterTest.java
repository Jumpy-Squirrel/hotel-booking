package info.rexis.hotelbooking.util.mappers;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class DateConverterTest {
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
        String output = new DateConverter().localeConvert(input, inputformat, outputformat);
        Assertions.assertThat(output).isEqualTo(expectedOutput);
    }
}
