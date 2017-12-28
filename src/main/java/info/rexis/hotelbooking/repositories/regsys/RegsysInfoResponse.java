package info.rexis.hotelbooking.repositories.regsys;

import lombok.Data;

@Data
public class RegsysInfoResponse {
    private boolean ok;
    private String nick;
    private String name;
    private String street;
    private String city;
    private String country;
    private String email;
}
