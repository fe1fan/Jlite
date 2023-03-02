package io.xka.jlite.web.options;

import io.xka.jlite.web.JliteApp;
import io.xka.jlite.web.runtime.JliteRuntime;
import io.xka.jlite.web.serializer.JsonAdopter;

public class Options {

    private String host = "localhost";

    private int port = 9090;

    private int maxThreads = 100;

    private int minThreads = 10;

    private int idleTimeout = 120;

    private Integer blockQueueSize = 1000;

    private SSLOptions sslOptions = new SSLOptions();

    private JsonAdopter.Engine serializer = JsonAdopter.Engine.JACKSON;

    private static Options create() {
        return new Options();
    }

    public static Options copy(Options options) {
        return Options.create()
                .host(options.getHost())
                .port(options.getPort())
                .maxThreads(options.getMaxThreads())
                .minThreads(options.getMinThreads())
                .idleTimeout(options.getIdleTimeout())
                .blockQueueSize(options.getBlockQueueSize())
                .sslOptions(options.getSslOptions())
                .serializer(options.getSerializer());
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

    public Options maxThreads(int maxThreads) {
        this.maxThreads = maxThreads;
        return this;
    }

    public Options minThreads(int minThreads) {
        this.minThreads = minThreads;
        return this;
    }

    public Options idleTimeout(int idleTimeout) {
        this.idleTimeout = idleTimeout;
        return this;
    }

    public Options blockQueueSize(int blockQueueSize) {
        this.blockQueueSize = blockQueueSize;
        return this;
    }

    public Options sslOptions(SSLOptions sslOptions) {
        this.sslOptions = sslOptions;
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

    public int getMaxThreads() {
        return maxThreads;
    }

    public int getMinThreads() {
        return minThreads;
    }

    public int getIdleTimeout() {
        return idleTimeout;
    }

    public Integer getBlockQueueSize() {
        return blockQueueSize;
    }

    public SSLOptions getSslOptions() {
        return sslOptions;
    }

    public JsonAdopter.Engine getSerializer() {
        return serializer;
    }
}
