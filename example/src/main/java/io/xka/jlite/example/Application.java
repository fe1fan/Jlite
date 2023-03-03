package io.xka.jlite.example;

import io.xka.jlite.web.serv.JliteServ;
import io.xka.jlite.web.serv.JliteServApp;
import io.xka.jlite.web.basic.serializer.JsonAdopter;

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
        app.get("/sse", ctl -> {
        });
    }
}
