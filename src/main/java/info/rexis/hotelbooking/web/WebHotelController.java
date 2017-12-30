package info.rexis.hotelbooking.web;

import info.rexis.hotelbooking.services.ReservationService;
import info.rexis.hotelbooking.services.config.HotelRoomProperties;
import info.rexis.hotelbooking.services.dto.ProcessStatus;
import info.rexis.hotelbooking.services.dto.ReservationDto;
import info.rexis.hotelbooking.web.mappers.ReservationMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/hotel/")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class WebHotelController {
    public static final String PAGE_HOTEL_FORM = "hotel-form";
    public static final String PAGE_HOTEL_LIST = "hotel-list";
    public static final String PAGE_HOTEL_DONE = "hotel-done";

    private ReservationService reservationService;
    private ReservationMapper reservationMapper;

    @GetMapping(PAGE_HOTEL_LIST)
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public String showListPage(Model model) {
        List<ReservationDto> reservations = reservationService.getEarliest20ByStatus(ProcessStatus.NEW);
        HotelRoomProperties hotelRoomProps = reservationService.getHotelRoomProperties();
        List<Map<String, String>> listing = reservations.stream()
                .map(r -> reservationMapper.listviewMapFromReservation(r, hotelRoomProps))
                .collect(Collectors.toList());

        model.addAttribute("listing", listing);
        return showPage(PAGE_HOTEL_LIST, model);
    }

    @GetMapping(PAGE_HOTEL_FORM)
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public String showFormPage(@RequestParam(name = "pk") String pk,
                               Model model) {
        ReservationDto reservation = reservationService.fetchAndLockForProcessing(pk);

        reservationMapper.modelFromReservation(model, reservation, reservationService.getHotelRoomProperties());
        model.addAttribute("pk", reservation.getPk());
        model.addAttribute("roomtypes", reservationService.getHotelRoomProperties().toListOfMaps());
        return showPage(PAGE_HOTEL_FORM, model);
    }

    private String showPage(String page, Model model) {
        model.addAttribute("currentpage", page);
        return page;
    }

    @PostMapping(PAGE_HOTEL_DONE)
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public String acceptAndShowListPage(@RequestParam(name = "pk") String pk, Model model) {
        reservationService.releaseAndSetToDone(pk);
        return showListPage(model);
    }

    @GetMapping(PAGE_HOTEL_DONE)
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public String resetAndShowListPage(@RequestParam(name = "pk") String pk, Model model) {
        reservationService.releaseAndPutBack(pk);
        return showListPage(model);
    }
}
