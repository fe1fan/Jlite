package io.xka.jlite.web.serv.connector;

import io.xka.jlite.web.serv.options.ServOptions;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.util.ssl.SslContextFactory;

public class HttpsConnector {

    public ServerConnector getConnector(Server server, ServOptions servOptions) {
        HttpConfiguration configuration = new HttpConfiguration(Configuration.getConfiguration(servOptions));

        SecureRequestCustomizer secureRequestCustomizer = new SecureRequestCustomizer();
        secureRequestCustomizer.setStsMaxAge(2000);

        configuration.addCustomizer(secureRequestCustomizer);

        SslContextFactory.Server sslContextFactory = new SslContextFactory.Server();
        sslContextFactory.setKeyStorePath(servOptions.getSslOptions().getKeystorePath());
        sslContextFactory.setKeyStorePassword(servOptions.getSslOptions().getKeystorePassword());
        sslContextFactory.setKeyManagerPassword(servOptions.getSslOptions().getKeyManagerPassword());

        ServerConnector connector = new ServerConnector(server, new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.asString()), new HttpConnectionFactory(configuration));
        connector.setPort(servOptions.getSslOptions().getSslPort());
        connector.setHost(servOptions.getHost());
        connector.setIdleTimeout(10_000);
        return connector;
    }
}
