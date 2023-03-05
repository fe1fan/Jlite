package io.xka.jlite.web.serv.connector;

import io.xka.jlite.web.serv.options.ServOptions;
import org.eclipse.jetty.server.HttpConfiguration;

public class Configuration {

    private static volatile HttpConfiguration configuration;

    protected static synchronized HttpConfiguration getConfiguration(ServOptions servOptions) {
        if (configuration == null) {
            configuration = new HttpConfiguration();
            configuration.setSecurePort(servOptions.getSslOptions().getSslPort());
            configuration.setOutputBufferSize(32768);
            configuration.setRequestHeaderSize(8192);
            configuration.setResponseHeaderSize(8192);
            configuration.setSendServerVersion(true);
            configuration.setSendDateHeader(false);
        }
        return configuration;
    }
}
