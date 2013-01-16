package eu.nets.utils.jetty.embedded.sample;

import eu.nets.utils.jetty.embedded.ContextPathConfig;
import eu.nets.utils.jetty.embedded.EmbeddedJettyBuilder;
import eu.nets.utils.jetty.embedded.EmbeddedSpringBuilder;
import org.apache.wicket.protocol.http.WebApplication;
import org.springframework.web.context.WebApplicationContext;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EventListener;

import static com.google.common.base.Throwables.propagate;
import static eu.nets.utils.jetty.embedded.EmbeddedSpringWsBuilder.createMessageDispatcherServlet;
import static eu.nets.utils.jetty.embedded.EmbeddedSpringBuilder.createSpringContextLoader;
import static eu.nets.utils.jetty.embedded.EmbeddedWicketBuilder.addWicketHandler;


/**
 * A Sample jetty builder
 *
 * @author Kristian Rosenvold
 */
public class SampleJettyBuilder extends EmbeddedJettyBuilder {
    private final WebApplicationContext ctx;

    private SampleJettyBuilder(ContextPathConfig context, boolean devMode, WebApplicationContext applicationContext) {
        super(context, devMode);
        ctx = applicationContext;
    }

    public static SampleJettyBuilder build(ContextPathConfig context, boolean serverMode, Class springConfigurationClass) {
        return new SampleJettyBuilder(context, !serverMode, EmbeddedSpringBuilder.createApplicationContext("VAS Core Application Context", springConfigurationClass));
    }

    public SampleJettyBuilder startStandardServices(Class<? extends WebApplication> wicketApplication) {
        getLogger().info("************************** Server starting: {} **************************",
                new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss.SSS]").format(new Date()));

        EventListener springContextLoader = createSpringContextLoader(ctx);
        addKeystore(10000);
        addWebservices(springContextLoader);
        createStandardClasspathResourceHandler("/res");
        addWicketHandler(this, "/wicket", springContextLoader, wicketApplication, true);
        try {
            startJetty();
            verifyServerStartup();
        } catch (Exception e) {
            //noinspection ThrowableResultOfMethodCallIgnored
            propagate(e);
        }
        return this;
    }

    public SampleJettyBuilder addWebservices(EventListener springContextLoader) {
        createRootServletContextHandler("")
                .addEventListener(springContextLoader)
                .addServlet(createMessageDispatcherServlet(WsServletConfiguration.class))
                  .mountAtPath("/helloService.wsdl")
                  .mountAtPath("/helloService");
        return this;
    }
}

