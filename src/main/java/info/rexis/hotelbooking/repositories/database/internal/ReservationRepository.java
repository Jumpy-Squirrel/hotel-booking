package info.rexis.hotelbooking.repositories.database.internal;

import info.rexis.hotelbooking.services.dto.ProcessStatus;
import info.rexis.hotelbooking.services.dto.ReservationDto;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ReservationRepository extends CrudRepository<ReservationDto, String> {
    List<ReservationDto> findById(int id);
    ReservationDto findByPk(String pk);
    List<ReservationDto> findTop20ByStatusOrderByPk(ProcessStatus status);
}
