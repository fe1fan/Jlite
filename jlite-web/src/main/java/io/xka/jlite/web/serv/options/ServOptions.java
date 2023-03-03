package io.xka.jlite.web.serv.options;

import io.xka.jlite.web.serv.JliteServApp;
import io.xka.jlite.web.basic.runtime.JliteRuntime;
import io.xka.jlite.web.basic.serializer.JsonAdopter;

public class ServOptions {

    private String host = "localhost";

    private int port = 9090;

    private SSLOptions sslOptions = new SSLOptions();

    private ThreadOptions threadOptions = new ThreadOptions();

    private CORSOptions corsOptions;

    private JsonAdopter.Engine serializer = JsonAdopter.Engine.JACKSON;

    private static ServOptions create() {
        return new ServOptions();
    }

    public static ServOptions copy(ServOptions servOptions) {
        return ServOptions.create()
                .host(servOptions.host)
                .port(servOptions.port)
                .sslOptions(SSLOptions.copy(servOptions.sslOptions))
                .threadOptions(ThreadOptions.copy(servOptions.threadOptions))
                .corsOptions(CORSOptions.copy(servOptions.corsOptions))
                .serializer(servOptions.serializer);
    }

    public JliteServApp quick() {
        JliteRuntime.setServOptions(this);
        return new JliteServApp();
    }


    /**
     * ---------------- settings ----------------
     */
    public ServOptions host(String host) {
        this.host = host;
        return this;
    }

    public ServOptions port(int port) {
        this.port = port;
        return this;
    }


    public ServOptions sslOptions(SSLOptions sslOptions) {
        this.sslOptions = sslOptions;
        return this;
    }

    public ServOptions threadOptions(ThreadOptions threadOptions) {
        this.threadOptions = threadOptions;
        return this;
    }

    public ServOptions serializer(JsonAdopter.Engine serializer) {
        this.serializer = serializer;
        return this;
    }

    public ServOptions corsOptions(CORSOptions corsOptions) {
        this.corsOptions = corsOptions;
        return this;
    }

    /**
     * ---------------- getter ----------------
     */
    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public SSLOptions getSslOptions() {
        return sslOptions;
    }


    public ThreadOptions getThreadOptions() {
        return threadOptions;
    }

    public CORSOptions getCorsOptions() {
        return corsOptions;
    }

    public JsonAdopter.Engine getSerializer() {
        return serializer;
    }

}
