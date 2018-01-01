package info.rexis.hotelbooking.web.logging;

import info.rexis.hotelbooking.util.exceptions.StacktraceNotNeeded;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Map;

/**
 * Provides request logging.
 *
 * Don't forget to also set the log level for this class to DEBUG to actually get any logging.
 *
 * (logging.level.package.Class=DEBUG)
 *
 * You will also need to set logging.ignorepattern. Requests will not be logged if they match the complete regex.
 */
@Component
public class RegexedRequestLoggingFilter extends CommonsRequestLoggingFilter {
    @Value("${logging.ignorepattern:.*}")
    protected String loggingIgnorePattern;

    private ErrorAttributes errorAttributes;

    @Autowired
    public RegexedRequestLoggingFilter(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    @Override
    protected boolean shouldLog(HttpServletRequest request) {
        return existingLogFilter(request)
                && additionalLogFilter(request);
    }

    protected boolean existingLogFilter(HttpServletRequest request) {
        return super.shouldLog(request);
    }

    protected boolean additionalLogFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return !uri.matches(loggingIgnorePattern);
    }

    public String createErrorMessage(HttpServletRequest request) {
        Map<String, Object> attributes = getErrorAttributes(request);
        String suffix = "]"
                + " path=" + attributes.get("path")
                + " status=" + attributes.get("status")
                + " error=" + attributes.get("error");
        String exceptionClassName = (String) attributes.get("exception");
        if (exceptionClassName != null) {
            suffix += " exception=" + exceptionClassName;
            if (showStacktrace(exceptionClassName)) {
                suffix += " stack trace follows\n"
                        + attributes.get("trace");
            } else {
                suffix += " stack trace suppressed for Exception marked StacktraceNotNeeded";
            }
        }
        return createMessage(request, "Error request [", suffix);
    }

    protected boolean showStacktrace(String exceptionClassName) {
        boolean showStacktrace = true;
        try {
            Type marker = StacktraceNotNeeded.class;
            Type[] interfaces = Class.forName(exceptionClassName).getGenericInterfaces();
            boolean hasMarker = Arrays.stream(interfaces).anyMatch(marker::equals);
            if (hasMarker) {
                showStacktrace = false;
            }
        } catch (Exception ignored) {
        }
        return showStacktrace;
    }

    protected Map<String, Object> getErrorAttributes(HttpServletRequest request) {
        RequestAttributes requestAttributes = new ServletRequestAttributes(request);
        return this.errorAttributes.getErrorAttributes(requestAttributes, true);
    }
}
