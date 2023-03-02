package io.xka.jlite.web.options;

import io.xka.jlite.web.JliteApp;
import io.xka.jlite.web.runtime.JliteRuntime;
import io.xka.jlite.web.serializer.JsonAdopter;

public class Options {

    private String host = "localhost";

    private int port = 9090;

    private SSLOptions sslOptions = new SSLOptions();

    private ThreadOptions threadOptions = new ThreadOptions();

    private JsonAdopter.Engine serializer = JsonAdopter.Engine.JACKSON;

    private static Options create() {
        return new Options();
    }

    public static Options copy(Options options) {
        return Options.create()
                .host(options.host)
                .port(options.port)
                .sslOptions(SSLOptions.copy(options.sslOptions))
                .threadOptions(ThreadOptions.copy(options.threadOptions))
                .serializer(options.serializer);
    }

    public JliteApp quick() {
        JliteRuntime.setOptions(this);
        return new JliteApp();
    }


    /**
     * ---------------- settings ----------------
     */
    public Options host(String host) {
        this.host = host;
        return this;
    }

    public Options port(int port) {
        this.port = port;
        return this;
    }


    public Options sslOptions(SSLOptions sslOptions) {
        this.sslOptions = sslOptions;
        return this;
    }

    public Options threadOptions(ThreadOptions threadOptions) {
        this.threadOptions = threadOptions;
        return this;
    }

    public Options serializer(JsonAdopter.Engine serializer) {
        this.serializer = serializer;
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

    public JsonAdopter.Engine getSerializer() {
        return serializer;
    }

}
