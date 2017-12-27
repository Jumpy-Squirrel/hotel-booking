package info.rexis.hotelbooking.repositories.email;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringWriter;

@Component
public class VelocityRenderer {
    public String renderTemplate(String templatePath, VelocityVariables variables) {
        VelocityEngine engine = new VelocityEngine();
        engine.setProperty(Velocity.RESOURCE_LOADER, "classpath");
        engine.addProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        engine.init();

        VelocityContext context = new VelocityContext(variables);

        Template velocityTemplate = engine.getTemplate(templatePath, "UTF-8");
        try (StringWriter writer = new StringWriter()) {
            velocityTemplate.merge(context, writer);
            return writer.toString();
        } catch (IOException ioe) {
            // this is really very unlikely
            return "Error: " + ioe.getMessage();
        }
    }
}
