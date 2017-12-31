package info.rexis.hotelbooking.repositories.database;

import info.rexis.hotelbooking.repositories.database.exceptions.ReservationNotFoundError;
import info.rexis.hotelbooking.repositories.database.internal.ReservationRepository;
import info.rexis.hotelbooking.services.dto.ProcessStatus;
import info.rexis.hotelbooking.services.dto.ReservationDto;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Collections;

public class DatabaseRepositoryTest {
    private ReservationRepository mockLowlevelRepo;
    private DatabaseRepository cut;

    @Before
    public void initializeMockAndCut() {
        mockLowlevelRepo = Mockito.mock(ReservationRepository.class);
        cut = new DatabaseRepository(mockLowlevelRepo);
    }

    @Test(expected = ReservationNotFoundError.class)
    public void loadReservationOrThrowExceptionCase() {
        Mockito.when(mockLowlevelRepo.findByPk("throw")).thenThrow(new RuntimeException("some database error"));
        cut.loadReservationOrThrow("throw");
    }

    @Test(expected = ReservationNotFoundError.class)
    public void loadReservationOrThrowNullCase() {
        Mockito.when(mockLowlevelRepo.findByPk("null")).thenReturn(null);
        cut.loadReservationOrThrow("null");
    }

    @Test
    public void saveReservationNullCase() {
        ReservationDto reservation = new ReservationDto();
        reservation.setPk("nomatter");
        reservation.setStatus(null);

        cut.saveReservation(reservation);
        Assertions.assertThat(reservation.getStatus()).isEqualTo(ProcessStatus.NEW);
    }

    @Test
    public void findLatestReservationOrNullNullCase() {
        Mockito.when(mockLowlevelRepo.findById(0)).thenReturn(null);
        Assertions.assertThat(cut.findLatestReservationOrNull(0)).isNull();
    }

    @Test
    public void findLatestReservationOrNullEmptyListCase() {
        Mockito.when(mockLowlevelRepo.findById(1)).thenReturn(Collections.emptyList());
        Assertions.assertThat(cut.findLatestReservationOrNull(1)).isNull();
    }

    @Test
    public void comparatorReverseSortedByPk() {
        ReservationDto a = new ReservationDto();
        ReservationDto b = new ReservationDto();
        Assertions.assertThat(cut.comparatorReverseSortedByPk(a, b)).isEqualTo(0);
    }

    @Test
    public void comparatorReverseSortedByPkNonNull() {
        ReservationDto a = new ReservationDto();
        ReservationDto b = new ReservationDto();
        a.setPk("2017-04-22");
        b.setPk("2018-01-02");
        Assertions.assertThat(cut.comparatorReverseSortedByPk(a, b)).isGreaterThan(0);
    }
}
