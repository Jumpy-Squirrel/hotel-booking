package info.rexis.hotelbooking.repositories.regsys.feign;

import info.rexis.hotelbooking.repositories.regsys.RegsysInfoResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "regsys", url = "${regsys.uri}", configuration = RegsysFeignConfiguration.class)
public interface RegsysFeignClient {
    @RequestMapping(method = RequestMethod.GET, value = "/service/room-booking-api", params = {"auth", "id", "token"})
    RegsysInfoResponse attendeeInfo(@RequestParam("auth") String auth, @RequestParam("id") int id, @RequestParam("token") String token);
}
