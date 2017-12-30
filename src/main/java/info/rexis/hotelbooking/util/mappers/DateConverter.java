package info.rexis.hotelbooking.util.mappers;

import org.springframework.stereotype.Component;

@Component
public class DateConverter {
    public String localeConvert(String input, String inputformat, String outputformat) {
        String iregex = inputformat
                .replaceFirst("dd", "([0-9]{2})")
                .replaceFirst("mm", "([0-9]{2})")
                .replaceFirst("yyyy", "([0-9]{4})")
                .replaceAll("\\.", "\\.");

        String daysAre = "\\$1";
        String monthsAre = "\\$2";
        if (inputformat.matches("mm.dd.yyyy")) {
            daysAre = "\\$2";
            monthsAre = "\\$1";
        }

        String oregex = outputformat
                .replaceFirst("dd", daysAre)
                .replaceFirst("mm", monthsAre)
                .replaceFirst("yyyy", "\\$3");
        return input.replaceFirst(iregex, oregex);
    }
}
