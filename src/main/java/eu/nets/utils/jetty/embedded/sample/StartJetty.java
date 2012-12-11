package eu.nets.utils.jetty.embedded.sample;


import eu.nets.utils.jetty.embedded.*;

import static eu.nets.utils.jetty.embedded.sample.SampleJettyBuilder.build;

public class StartJetty {
    public static void main(String... args) {

        boolean onServer = EmbeddedJettyBuilder.isStartedWithAppassembler();

        ContextPathConfig webAppSource = onServer ? new PropertiesFileConfig() : new StaticConfig("/sample", 8080);

        final SampleJettyBuilder builder = build(webAppSource, onServer, ApplicationConfiguration.class);

        if (onServer){
            StdoutRedirect.tieSystemOutAndErrToLog();
            builder.addHttpAccessLogAtRoot();
        }

        builder.startStandardServices(SampleWicketApplication.class);

        if (!onServer){
            String url = "/wicket/homePage";
            //url = "/res/static/StartEnrol.html";
            builder.startBrowserStopWithAnyKey(url);
        }
    }
}


