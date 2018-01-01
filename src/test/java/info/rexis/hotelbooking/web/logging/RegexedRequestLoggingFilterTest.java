package info.rexis.hotelbooking.web.logging;

import info.rexis.hotelbooking.repositories.regsys.exceptions.RegsysAuthError;
import info.rexis.hotelbooking.repositories.regsys.exceptions.RegsysClientError;
import info.rexis.hotelbooking.util.exceptions.StacktraceNotNeeded;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class RegexedRequestLoggingFilterTest {
    private class ClassUnderTest extends RegexedRequestLoggingFilter {
        ClassUnderTest(ErrorAttributes errorAttributes, String loggingIgnorePattern) {
            super(errorAttributes);
            this.loggingIgnorePattern = loggingIgnorePattern;
        }

        @Override
        protected boolean existingLogFilter(HttpServletRequest request) {
            return true;
        }

        @Override
        protected Map<String, Object> getErrorAttributes(HttpServletRequest request) {
            Map<String, Object> attr = new HashMap<>();
            Stream.of("path", "status", "error", "trace").forEach(v -> attr.put(v, v));
            attr.put("exception", RegsysClientError.class.getCanonicalName());
            return attr;
        }

        @Override
        protected String createMessage(HttpServletRequest request, String prefix, String suffix) {
            return "prefix: " + prefix + " / suffix: " + suffix;
        }
    }

    private ClassUnderTest cut;
    private HttpServletRequest mockRequest;

    @Before
    public void setup() {
        cut = new ClassUnderTest(null, "matching");
        mockRequest = Mockito.mock(HttpServletRequest.class);
    }

    @Test
    public void shouldNotLogIfUrlMatchesIgnorepattern() {
        Mockito.when(mockRequest.getRequestURI()).thenReturn("matching");
        Assertions.assertThat(cut.shouldLog(mockRequest)).isEqualTo(false);
    }

    @Test
    public void shouldLogIfUrlDoesNotMatchIgnorepattern() {
        Mockito.when(mockRequest.getRequestURI()).thenReturn("no match");
        Assertions.assertThat(cut.shouldLog(mockRequest)).isEqualTo(true);
    }

    @Test
    public void shouldShowStacktraceForExceptionNotImplementingInterfaceStacktraceNotNeeded() {
        Exception unmarkedException = new RegsysClientError("whatever", 404);
        Assertions.assertThat(unmarkedException instanceof StacktraceNotNeeded).isEqualTo(false);

        Assertions.assertThat(cut.showStacktrace(unmarkedException.getClass().getCanonicalName()))
                .isEqualTo(true);
    }

    @Test
    public void shouldNotShowStacktraceForExceptionImplementingInterfaceStacktraceNotNeeded() {
        Exception markedException = new RegsysAuthError("whatever");
        Assertions.assertThat(markedException instanceof StacktraceNotNeeded).isEqualTo(true);

        Assertions.assertThat(cut.showStacktrace(markedException.getClass().getCanonicalName()))
                .isEqualTo(false);
    }

    @Test
    public void shouldShowStacktraceForInvalidClassname() {
        Assertions.assertThat(cut.showStacktrace("thisClassnameDoesNotExist"))
                .isEqualTo(true);
    }

    @Test
    public void shouldLogAsExpectedForUnmarkedException() {
        String message = cut.createErrorMessage(mockRequest);
        Assertions.assertThat(message).doesNotContain("stack trace suppressed for");
    }
}
