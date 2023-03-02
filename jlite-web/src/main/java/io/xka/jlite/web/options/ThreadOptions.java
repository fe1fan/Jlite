package io.xka.jlite.web.options;

public class ThreadOptions {

    private int maxThreads = 100;

    private int minThreads = 10;

    private int idleTimeout = 120;

    private Integer blockQueueSize = 1000;


    public static ThreadOptions create() {
        return new ThreadOptions();
    }

    public static ThreadOptions copy(ThreadOptions threadOptions) {
        return ThreadOptions.create()
                .maxThreads(threadOptions.maxThreads)
                .minThreads(threadOptions.minThreads)
                .idleTimeout(threadOptions.idleTimeout)
                .blockQueueSize(threadOptions.blockQueueSize);
    }

    public ThreadOptions maxThreads(int maxThreads) {
        this.maxThreads = maxThreads;
        return this;
    }

    public ThreadOptions minThreads(int minThreads) {
        this.minThreads = minThreads;
        return this;
    }

    public ThreadOptions idleTimeout(int idleTimeout) {
        this.idleTimeout = idleTimeout;
        return this;
    }

    public ThreadOptions blockQueueSize(Integer blockQueueSize) {
        this.blockQueueSize = blockQueueSize;
        return this;
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
}
