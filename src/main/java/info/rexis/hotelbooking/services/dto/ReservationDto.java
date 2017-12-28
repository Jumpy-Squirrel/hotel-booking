package info.rexis.hotelbooking.services.dto;

import lombok.Data;

@Data
public class ReservationDto {
    private int roomsize;

    private String name1;
    private String street1;
    private String city1;
    private String email1;

    private String name2;
    private String street2;
    private String city2;
    private String email2;

    private String name3;
    private String street3;
    private String city3;
    private String email3;

    private String dateformat;
    private String arrival;
    private String departure;

    private int roomtype;

    private String comments;

    private String understood;

    private int id;

    private String token;
}
