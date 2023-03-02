package io.xka.jlite.example;

import io.xka.jlite.web.Jlite;
import io.xka.jlite.web.JliteApp;
import io.xka.jlite.web.options.SSLOptions;
import io.xka.jlite.web.serializer.JsonAdopter;

import java.util.HashMap;

public class Application {
    public static void main(String[] args) {
        //create from options
        JliteApp app = Jlite.options()
                .host("localhost")
                .port(8080)
                .maxThreads(100)
                .minThreads(10)
                .serializer(JsonAdopter.Engine.JACKSON)
                .sslOptions(
                        Jlite.sslOptions()
                                .enableSSL()
                                .sslPort(8081)
                                .keystorePath("keystore.jks")
                                .keystorePassword("password")
                                .keyManagerPassword("password")
                )
                .quick();
        app.run();
        app.use("/json", hdl -> {
            String authorization = hdl.getHeader("authorization");
            if (authorization == null || authorization.isBlank()) {
                //error
                hdl.result("Unauthorized");
                return false;
            }
            return true;
        });
        app.get("/xml/hello", ctl -> ctl.json(new HashMap<>() {
            {
                put("hello", "world");
            }
        }));
        app.get("/json/hello", ctl -> ctl.json(new HashMap<>() {
            {
                put("hello", "world");
            }
        }));
//        app.stop();
    }
}
