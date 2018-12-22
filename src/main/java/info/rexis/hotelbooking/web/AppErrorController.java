package info.rexis.hotelbooking.web;

import info.rexis.hotelbooking.web.logging.RegexedRequestLoggingFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Basic Controller which is called for unhandled errors.
 */
@Controller
public class AppErrorController implements ErrorController {
    private static final String ERROR_PATH = "/error";

    protected final Log logger = LogFactory.getLog(getClass());

    private ErrorAttributes errorAttributes;
    private RegexedRequestLoggingFilter requestLoggingFilter;

    public AppErrorController(ErrorAttributes errorAttributes, RegexedRequestLoggingFilter requestLoggingFilter) {
        this.errorAttributes = errorAttributes;
        this.requestLoggingFilter = requestLoggingFilter;
    }

    /**
     * HTML Error View.
     */
    @RequestMapping(value = ERROR_PATH, produces = {MediaType.TEXT_HTML_VALUE})
    public ModelAndView errorHtml(HttpServletRequest request, WebRequest webRequest) {
        logger.warn(requestLoggingFilter.createErrorMessage(request));
        return new ModelAndView("error", getErrorAttributes(webRequest, false));
    }

    /**
     * Supports JSON, XML, other formats.
     */
    @RequestMapping(value = ERROR_PATH, produces = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.TEXT_XML_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseBody
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request, WebRequest webRequest) {
        logger.warn(requestLoggingFilter.createErrorMessage(request));
        Map<String, Object> body = getErrorAttributes(webRequest, false);
        HttpStatus status = getStatus(request);
        return new ResponseEntity<>(body, status);
    }

    @Override
    public String getErrorPath() {
        return ERROR_PATH;
    }

    private Map<String, Object> getErrorAttributes(WebRequest webRequest, boolean includeStackTrace) {
        return this.errorAttributes.getErrorAttributes(webRequest, includeStackTrace);
    }

    protected HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request
                .getAttribute("javax.servlet.error.status_code");
        if (statusCode != null) {
            try {
                return HttpStatus.valueOf(statusCode);
            } catch (Exception ignore) {
            }
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
