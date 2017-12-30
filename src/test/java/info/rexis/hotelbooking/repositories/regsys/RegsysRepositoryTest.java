package info.rexis.hotelbooking.repositories.regsys;

import info.rexis.hotelbooking.repositories.regsys.exceptions.RegsysParameterError;
import info.rexis.unscanned.MockRegsysFeignClientConfig;
import org.junit.Test;

public class RegsysRepositoryTest {
    private static RegsysRepository cut = new RegsysRepository(new MockRegsysFeignClientConfig.MockRegsysFeignClient());

    @Test(expected = RegsysParameterError.class)
    public void shouldThrowOnInvalidId() {
        cut.getPersonalInfo(piReq(0, "any"));
    }

    @Test(expected = RegsysParameterError.class)
    public void shouldThrowOnNegativeId() {
        cut.getPersonalInfo(piReq(-3, "any"));
    }

    @Test(expected = RegsysParameterError.class)
    public void shouldThrowOnNullToken() {
        cut.getPersonalInfo(piReq(17, null));
    }

    @Test(expected = RegsysParameterError.class)
    public void shouldThrowOnEmptyToken() {
        cut.getPersonalInfo(piReq(17, ""));
    }

    private PersonalInfoRequestDto piReq(int id, String token) {
        return PersonalInfoRequestDto.builder().id(id).token(token).build();
    }
}
