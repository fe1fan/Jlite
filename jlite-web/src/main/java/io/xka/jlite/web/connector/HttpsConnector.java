package io.xka.jlite.web.connector;

import io.xka.jlite.web.options.Options;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.util.ssl.SslContextFactory;

public class HttpsConnector {

    public ServerConnector getConnector(Server server, Options options) {
        HttpConfiguration configuration = new HttpConfiguration(Configuration.getConfiguration(options));

        SecureRequestCustomizer secureRequestCustomizer = new SecureRequestCustomizer();
        secureRequestCustomizer.setStsMaxAge(2000);

        configuration.addCustomizer(secureRequestCustomizer);

        SslContextFactory.Server sslContextFactory = new SslContextFactory.Server();
        sslContextFactory.setKeyStorePath(options.getSslOptions().getKeystorePath());
        sslContextFactory.setKeyStorePassword(options.getSslOptions().getKeystorePassword());
        sslContextFactory.setKeyManagerPassword(options.getSslOptions().getKeyManagerPassword());

        ServerConnector connector = new ServerConnector(server, new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.asString()), new HttpConnectionFactory(configuration));
        connector.setPort(options.getSslOptions().getSslPort());
        connector.setHost(options.getHost());
        connector.setIdleTimeout(500000);
        return connector;
    }
}
