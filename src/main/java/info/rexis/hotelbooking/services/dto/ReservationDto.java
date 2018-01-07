package info.rexis.hotelbooking.services.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import java.util.Date;

@Data
@Entity
@Table(name = "reservations",
       indexes = {@Index(name = "id_idx", columnList = "id"),
       @Index(name = "status_idx", columnList = "status")})
@AllArgsConstructor
@NoArgsConstructor
public class ReservationDto {
    private int roomsize;

    private String name1;
    private String street1;
    private String city1;
    private String country1;
    private String email1;
    private String phone1;

    private String name2;
    private String street2;
    private String city2;
    private String country2;
    private String email2;
    private String phone2;

    private String name3;
    private String street3;
    private String city3;
    private String country3;
    private String email3;
    private String phone3;

    private String dateformat;
    private String arrival;
    private String departure;

    private int roomtype;

    @Column(length = 8000)
    private String comments;

    private String understood;

    private int id;

    private String token;

    @Id
    private String pk;

    private ProcessStatus status = ProcessStatus.NEW;

    private Date processed;

    private String session;
}
