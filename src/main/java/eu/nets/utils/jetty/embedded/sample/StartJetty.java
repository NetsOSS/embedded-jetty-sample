package eu.nets.utils.jetty.embedded.sample;


import eu.nets.utils.jetty.embedded.PropertiesFileAppSource;
import eu.nets.utils.jetty.embedded.StdoutRedirect;

import static eu.nets.utils.jetty.embedded.sample.SampleJettyBuilder.devEnv;
import static eu.nets.utils.jetty.embedded.sample.SampleJettyBuilder.server;

public class StartJetty {
    public static void main(String... args) {

        final SampleJettyBuilder sampleJettyBuilder;

        if (isRunningOnServer()){
            StdoutRedirect.tieSystemOutAndErrToLog();
            PropertiesFileAppSource webAppSource = new PropertiesFileAppSource();
            sampleJettyBuilder = server(webAppSource.getContextPath(), webAppSource.getPort(), ApplicationConfiguration.class);
            sampleJettyBuilder.addHttpAccessLog();
        } else {
            sampleJettyBuilder = devEnv("/vas", 8080, ApplicationConfiguration.class);
        }

        sampleJettyBuilder.startStandardServices(SampleWicketApplication.class);

        if (!isRunningOnServer()){
            String url = "/homePage";
            //url = "/res/static/StartEnrol.html";
            sampleJettyBuilder.startBrowserStopWithAnyKey(url);
        }
    }

    private static boolean isRunningOnServer(){
        return System.getProperty("app.home") != null;  // Started with appassembly
    }
}


