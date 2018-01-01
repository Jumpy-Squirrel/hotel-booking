package info.rexis.hotelbooking.web.mappers;

import info.rexis.hotelbooking.services.dto.EmailDto;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.io.UnsupportedEncodingException;

public class MailtoLinkBuilderTest {
    @Test
    public void shouldIgnoreExceptionsAndReturnNull() {
        MailtoLinkBuilder cut = new MailtoLinkBuilder() {
            @Override
            protected String urlEncodeUtf8(String value) throws UnsupportedEncodingException {
                throw new UnsupportedEncodingException();
            }
        };
        EmailDto email = new EmailDto();
        email.setBody("some body");
        email.setSubject("some subject");
        email.setRecipient("someone@mailinator.com");

        Assertions.assertThat(cut.buildOrNullIgnoringExceptions(email)).isNull();
    }
}
