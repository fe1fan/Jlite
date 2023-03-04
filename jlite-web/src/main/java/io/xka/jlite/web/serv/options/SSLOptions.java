package io.xka.jlite.web.serv.options;

public class SSLOptions {

    private boolean enableSSL = false;

    private Integer sslPort = 9091;

    private String keystorePath = "keystore.jks";

    private String keystorePassword = "password";

    private String keyManagerPassword = "password";

    public static SSLOptions create() {
        return new SSLOptions();
    }

    public static SSLOptions copy(SSLOptions sslOptions) {
        SSLOptions copy = SSLOptions.create()
                .sslPort(sslOptions.sslPort)
                .keystorePath(sslOptions.keystorePath)
                .keystorePassword(sslOptions.keystorePassword)
                .keyManagerPassword(sslOptions.keyManagerPassword);
        if (sslOptions.isEnableSSL()) {
            copy.enableSSL();
        }
        return copy;
    }

    public SSLOptions enableSSL() {
        this.enableSSL = true;
        return this;
    }

    public SSLOptions sslPort(Integer sslPort) {
        this.sslPort = sslPort;
        return this;
    }

    public SSLOptions keystorePath(String keystorePath) {
        this.keystorePath = keystorePath;
        return this;
    }

    public SSLOptions keystorePassword(String keystorePassword) {
        this.keystorePassword = keystorePassword;
        return this;
    }

    public SSLOptions keyManagerPassword(String keyManagerPassword) {
        this.keyManagerPassword = keyManagerPassword;
        return this;
    }

    public boolean isEnableSSL() {
        return enableSSL;
    }

    public Integer getSslPort() {
        return sslPort;
    }

    public String getKeystorePath() {
        return keystorePath;
    }

    public String getKeystorePassword() {
        return keystorePassword;
    }

    public String getKeyManagerPassword() {
        return keyManagerPassword;
    }
}
