package info.rexis.hotelbooking.repositories.email;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.function.Consumer;

public class VelocityRendererTest {
    @Test
    public void shouldReturnErrorMessageOnException() {
        VelocityRenderer cut = new VelocityRenderer() {
            @Override
            protected String writeToString(Consumer<StringWriter> author) throws IOException {
                throw new IOException("message");
            }
        };

        String output = cut.writeToStringOrPrintError(writer -> { });
        Assertions.assertThat(output).isEqualTo("Error: message");
    }
}
