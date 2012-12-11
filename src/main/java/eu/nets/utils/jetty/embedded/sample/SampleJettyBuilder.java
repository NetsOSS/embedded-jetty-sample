package eu.nets.utils.jetty.embedded.sample;

import eu.nets.utils.jetty.embedded.EmbeddedJettyBuilder;
import eu.nets.utils.jetty.embedded.EmbeddedSpringBuilder;
import eu.nets.utils.jetty.embedded.EmbeddedWicketBuilder;
import org.apache.wicket.protocol.http.WebApplication;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.ws.transport.http.MessageDispatcherServlet;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EventListener;

import static com.google.common.base.Throwables.propagate;


/**
 * A Vas Specific jetty builder
 *
 * @author Kristian Rosenvold
 */
public class SampleJettyBuilder extends EmbeddedJettyBuilder {
    private final WebApplicationContext ctx;

    private SampleJettyBuilder(String contextPath, int port, boolean devMode, WebApplicationContext applicationContext) {
        super(contextPath, port, devMode);
        ctx = applicationContext;
    }

    public static SampleJettyBuilder devEnv(final String context, final int port, Class springConfigurationClass) {
        return new SampleJettyBuilder(context, port, true, EmbeddedSpringBuilder.createApplicationContext(springConfigurationClass, "VAS Core Application Context"));
    }

    public static SampleJettyBuilder server(String context, int port, Class springConfigurationClass) {
        return new SampleJettyBuilder(context, port, false, EmbeddedSpringBuilder.createApplicationContext(springConfigurationClass, "VAS Core Application Context"));
    }

    public static SampleJettyBuilder emptyVasForTest(int port){
        return SampleJettyBuilder.devEnv("/sample", port, ApplicationConfiguration.class);
    }

    public SampleJettyBuilder addWebsocketServices() {
        return this;
    }

    public void addWicket(Class<? extends WebApplication> wicketApplication, WebApplicationContext springWebApplicationContext) {
        EventListener springContextLoader = EmbeddedSpringBuilder.createSpringContextLoader(springWebApplicationContext);
        EmbeddedWicketBuilder.addWicketHandler(this, "/portal", springContextLoader, wicketApplication, true);
    }


    public SampleJettyBuilder startStandardServices(Class<? extends WebApplication> wicketApplication) {
        getLogger().info("************************** Server starting: {} **************************",
                new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss.SSS]").format(new Date()));

        addKeystore(10000);
        addWebsocketServices();
        addWebservices();
        createStandardClasspathResourceHandler("/res");
        addWicket(wicketApplication, ctx);
        try {
            startJetty();
            verifyServerStartup();
        } catch (Exception e) {
            //noinspection ThrowableResultOfMethodCallIgnored
            propagate(e);
        }
        return this;
    }


    public SampleJettyBuilder addWebservices() {

        EventListener springContextLoader = EmbeddedSpringBuilder.createSpringContextLoader(ctx);
        MessageDispatcherServlet messageDispatcherServlet = EmbeddedSpringBuilder.createMessageDispatcherServlet(WsServletConfiguration.class);

        createRootServletContextHandler("")
                .addEventListener(springContextLoader)
                .addServlet(messageDispatcherServlet, "/helloService.wsdl")
                    .mountAtPath("/helloService");
        return this;
    }

    public SampleJettyBuilder addHttpAccessLog() {
        addHttpAccessLogAtRoot();
        return this;
    }


}

