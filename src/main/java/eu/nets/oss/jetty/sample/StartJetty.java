package eu.nets.oss.jetty.sample;


import eu.nets.utils.jetty.embedded.*;
import org.springframework.web.context.WebApplicationContext;

import java.util.EventListener;

import static com.google.common.base.Throwables.propagate;
import static eu.nets.utils.jetty.embedded.EmbeddedSpringBuilder.createSpringContextLoader;
import static eu.nets.utils.jetty.embedded.EmbeddedSpringWsBuilder.createMessageDispatcherServlet;
import static eu.nets.utils.jetty.embedded.EmbeddedWicketBuilder.addWicketHandler;

public class StartJetty {
    public static void main(String... args) {

        boolean onServer = EmbeddedJettyBuilder.isStartedWithAppassembler();

        ContextPathConfig webAppSource = onServer ? new PropertiesFileConfig() : new StaticConfig("/jettySample", 8080);
        final EmbeddedJettyBuilder builder = new EmbeddedJettyBuilder(webAppSource, !onServer);

        if (onServer){
            StdoutRedirect.tieSystemOutAndErrToLog();
            builder.addHttpAccessLogAtRoot();
        }

        WebApplicationContext ctx = EmbeddedSpringBuilder.createApplicationContext("VAS Core Application Context", ApplicationConfiguration.class);
        EventListener springContextLoader = createSpringContextLoader(ctx);
        builder.addKeystore(10000);

        builder.createRootServletContextHandler("")
                .addEventListener(springContextLoader)
                    .addServlet(createMessageDispatcherServlet(WsServletConfiguration.class))
                        .mountAtPath("/helloService.wsdl")
                        .mountAtPath("/helloService");


        // Option 1: Separate context
        ClasspathResourceHandler rh1 = builder.createNetsStandardClasspathResourceHandler();
        builder.createRootServletContextHandler("/res").setResourceHandler(rh1);

        // Alt 2: Put resource handler on same path as wicket
        ClasspathResourceHandler rh2 = builder.createNetsStandardClasspathResourceHandler();
        EmbeddedJettyBuilder.ServletContextHandlerBuilder servletContextHandlerBuilder = addWicketHandler(builder, "/wicket", springContextLoader, SampleWicketApplication.class, true);
        // Temporary disabled while we're waiting for the outcome of a jetty bug on this
        //servletContextHandlerBuilder.setResourceHandler(rh2);
        try {
            builder.startJetty();
            builder.verifyServerStartup();
        } catch (Exception e) {
            //noinspection ThrowableResultOfMethodCallIgnored
            propagate(e);
        }

        if (!onServer){
            String url = "/wicket/homePage";
            builder.startBrowserStopWithAnyKey(url);
        }
    }
}


