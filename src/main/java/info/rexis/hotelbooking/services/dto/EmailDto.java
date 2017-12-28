package info.rexis.hotelbooking.services.dto;

import lombok.Data;

@Data
public class EmailDto {
    private String recipient;
    private String subject;
    private String body;
}
