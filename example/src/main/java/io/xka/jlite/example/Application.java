package io.xka.jlite.example;

import io.xka.jlite.web.cli.JliteCli;
import io.xka.jlite.web.cli.JliteCliApp;
import io.xka.jlite.web.cli.components.HttpComponents;
import io.xka.jlite.web.serv.JliteServ;
import io.xka.jlite.web.serv.JliteServApp;
import io.xka.jlite.web.basic.serializer.JsonAdopter;

import java.util.ArrayList;
import java.util.HashMap;

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
                .sslOptions(
                        JliteServ.sslOptions()
                                .enableSSL()
                                .sslPort(8081)
                                .keystorePath("keystore.jks")
                                .keystorePassword("password")
                                .keyManagerPassword("password")
                )
                .quick();
        app.run();
        JliteCliApp cli = JliteCli.options()
                .serializer(JsonAdopter.Engine.JACKSON)
                .quick();
        app.use("/", hdl -> {
            String authorization = hdl.getHeader("authorization");
            if (authorization == null || authorization.isBlank()) {
                //error
                hdl.result("Unauthorized");
                return false;
            }
            return true;
        });
    }
}
