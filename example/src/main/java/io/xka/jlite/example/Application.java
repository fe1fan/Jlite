package io.xka.jlite.example;

import io.xka.jlite.web.serv.JliteServ;
import io.xka.jlite.web.serv.JliteServApp;
import io.xka.jlite.web.basic.serializer.JsonAdopter;

import java.util.concurrent.ConcurrentHashMap;

public class Application {
    public static void main(String[] args) {
        //create from options
        JliteServApp app = JliteServ.options()
                .host("localhost")
                .port(8080)
                .threadOptions(
                        JliteServ.threadOptions()
                                .maxThreads(100)
                                .minThreads(10)
                                .idleTimeout(1000)
                )
                .corsOptions(
                        JliteServ.corsOptions()
                                .enable(true)
                                .allowCredentials("*")
                                .allowHeaders("Content-Type")
                                .allowMethods("GET, POST, PUT, DELETE, OPTIONS")
                                .allowOrigin("http://localhost:63342")
                )
                .serializer(JsonAdopter.Engine.JACKSON)
//                .sslOptions(
//                        JliteServ.sslOptions()
//                                .enableSSL()
//                                .sslPort(8081)
//                                .keystorePath("keystore.jks")
//                                .keystorePassword("password")
//                                .keyManagerPassword("password")
//                )
                .quick();
        app.run();
//        ConcurrentHashMap<String, >
        app.get("/sse", ctl -> {
            for (int i = 0; i < 10; i++) {
//                ctl.sse("hello", "world");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            ctl.finishSse();
        });
    }
}
