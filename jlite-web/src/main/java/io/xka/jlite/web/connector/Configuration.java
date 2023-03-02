package io.xka.jlite.web.connector;

import io.xka.jlite.web.options.Options;
import org.eclipse.jetty.server.HttpConfiguration;

public class Configuration {

    private static volatile HttpConfiguration configuration;

    protected static synchronized HttpConfiguration getConfiguration(Options options) {
        if (configuration == null) {
            configuration = new HttpConfiguration();
            configuration.setSecurePort(options.getSslOptions().getSslPort());
            configuration.setOutputBufferSize(32768);
            configuration.setRequestHeaderSize(8192);
            configuration.setResponseHeaderSize(8192);
            configuration.setSendServerVersion(true);
            configuration.setSendDateHeader(false);
        }
        return configuration;
    }
}
