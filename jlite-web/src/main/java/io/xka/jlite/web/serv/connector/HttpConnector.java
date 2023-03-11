package io.xka.jlite.web.serv.connector;

import io.xka.jlite.web.serv.options.ServOptions;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;

public class HttpConnector {

    public ServerConnector getConnector(Server server, ServOptions servOptions) {
        HttpConfiguration configuration = Configuration.getConfiguration(servOptions);
        ServerConnector connector = new ServerConnector(server, new HttpConnectionFactory(configuration));
        connector.setPort(servOptions.getPort());
        connector.setHost(servOptions.getHost());
        connector.setIdleTimeout(10_000);
        return connector;
    }
}
