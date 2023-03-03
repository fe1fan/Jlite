package io.xka.jlite.web.serv;

import io.xka.jlite.web.serv.connector.HttpConnector;
import io.xka.jlite.web.serv.connector.HttpsConnector;
import io.xka.jlite.web.serv.control.*;
import io.xka.jlite.web.serv.options.SSLOptions;
import io.xka.jlite.web.basic.runtime.JliteRuntime;
import io.xka.jlite.web.serv.options.ServOptions;
import jakarta.servlet.MultipartConfigElement;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.BlockingArrayQueue;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class JliteServApp {

    public static final String VERSION = "alpha-0.0.1";

    Logger logger = LoggerFactory.getLogger(JliteServApp.class);

    enum Status {
        STARING, RUNNING, STOPPING, STOPPED
    }

    protected volatile Status STATUS = Status.STOPPED;

    private final Server server;

    private final ServOptions options;


    private void init() {
        SSLOptions sslOptions = options.getSslOptions();
        List<ServerConnector> connectors = new ArrayList<>(2);
        if (sslOptions.isEnableSSL()) {
            HttpsConnector httpsConnector = new HttpsConnector();
            connectors.add(httpsConnector.getConnector(server, options));
        }
        HttpConnector httpConnector = new HttpConnector();
        connectors.add(httpConnector.getConnector(server, options));
        this.server.setConnectors(connectors.toArray(new ServerConnector[0]));
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        ServletHolder servletHolder = context.addServlet(HttpServletControl.class, "/*");
        servletHolder.getRegistration().setMultipartConfig(
                new MultipartConfigElement(System.getProperty("java.io.tmpdir"))
        );
        this.server.setHandler(context);
    }


    public JliteServApp() {
        this.options = JliteRuntime.getServOptions();
        QueuedThreadPool threadPool = new QueuedThreadPool(
                options.getThreadOptions().getMaxThreads(),
                options.getThreadOptions().getMinThreads(),
                options.getThreadOptions().getIdleTimeout(),
                new BlockingArrayQueue<>(options.getThreadOptions().getBlockQueueSize())
        );
        this.server = new Server(threadPool);
        this.init();
    }

    private void log() {
        logger.info("\n     ____.__  .__  __          \n" +
                "    |    |  | |__|/  |_  ____  \n" +
                "    |    |  | |  \\   __\\/ __ \\ \n" +
                "/\\__|    |  |_|  ||  | \\  ___/ \n" +
                "\\________|____/__||__|  \\___  >\n" +
                "                            \\/  fast and simple web framework :)");
        logger.info("use jlite version: " + VERSION);
        logger.info("use jlite json serializer adopter: {}", options.getSerializer());
        logger.info("enable ssl: {}", options.getSslOptions().isEnableSSL());
    }


    public synchronized JliteServApp run() {
        if (this.STATUS != Status.STOPPED) {
            throw new RuntimeException("server is running");
        }
        logger.info("Jlite Server Starting...");
        this.log();
        try {
            this.server.start();
            new Thread(() -> {
                try {
                    this.server.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }).start();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    logger.info("Jlite Server Stopping...");
                    this.server.stop();
                    logger.info("Jlite Server Stopped");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }));
            this.STATUS = Status.RUNNING;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        logger.info("\n\nJlite Server Started Successfully On \nhttp://{}:{}\nhttps://{}:{}\n", options.getHost(), options.getPort(), options.getHost(), options.getSslOptions().getSslPort());
        return this;
    }

    public JliteServApp stop() {
        try {
            this.server.stop();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    private void addControl(String path, HttpMethod method, Consumer<IControl> consumer) {
        logger.info("jlite register control mapping: {}, {}", method, path);
        ControlFactory.register(path, method, consumer);
    }

    public void get(String path, Consumer<IControl> consumer) {
        this.addControl(path, HttpMethod.GET, consumer);
    }

    public void post(String path, Consumer<IControl> consumer) {
        this.addControl(path, HttpMethod.POST, consumer);
    }

    public void put(String path, Consumer<IControl> consumer) {
        this.addControl(path, HttpMethod.PUT, consumer);
    }

    public void delete(String path, Consumer<IControl> consumer) {
        this.addControl(path, HttpMethod.DELETE, consumer);
    }

    public void patch(String path, Consumer<IControl> consumer) {
        this.addControl(path, HttpMethod.PATCH, consumer);
    }

    public void head(String path, Consumer<IControl> consumer) {
        this.addControl(path, HttpMethod.HEAD, consumer);
    }

    public void options(String path, Consumer<IControl> consumer) {
        this.addControl(path, HttpMethod.OPTIONS, consumer);
    }


    private void addHandler(String path, Function<IHandler, Boolean> handler) {
        logger.info("jlite register handler mapping: {}", path);
        HandlerFactory.register(path, handler);
    }

    public void use(String path, Function<IHandler, Boolean> handler) {
        this.addHandler(path, handler);
    }
}
