package io.xka.jlite.web.connector;

import io.xka.jlite.web.options.Options;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;

public class HttpConnector {

    public ServerConnector getConnector(Server server, Options options) {
        HttpConfiguration configuration = Configuration.getConfiguration(options);
        ServerConnector connector = new ServerConnector(server, new HttpConnectionFactory(configuration));
        connector.setPort(options.getPort());
        connector.setHost(options.getHost());
        connector.setIdleTimeout(50000);
        return connector;
    }
}
