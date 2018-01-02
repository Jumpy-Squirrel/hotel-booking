package info.rexis.hotelbooking.repositories.database.internal;

import info.rexis.hotelbooking.services.dto.ProcessStatus;
import info.rexis.hotelbooking.services.dto.ReservationDto;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface ReservationRepository extends CrudRepository<ReservationDto, String> {
    List<ReservationDto> findById(int id);
    ReservationDto findByPk(String pk);
    List<ReservationDto> findTop20ByStatusOrderByPk(ProcessStatus status);

    @Modifying
    @Query("UPDATE ReservationDto R "
            + "SET R.status = :newstatus, R.processed = :timestamp, R.session = :session "
            + "WHERE R.pk = :pk AND R.status = :oldstatus AND R.session IS NULL")
    void lockReservationForProcessing(@Param("pk") String pk,
                                      @Param("oldstatus") ProcessStatus oldstatus,
                                      @Param("newstatus") ProcessStatus newstatus,
                                      @Param("session") String session,
                                      @Param("timestamp") Date timestamp);
}
