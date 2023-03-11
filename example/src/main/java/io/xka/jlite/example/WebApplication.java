package io.xka.jlite.example;

import io.xka.jlite.web.serv.JliteServ;
import io.xka.jlite.web.serv.JliteServApp;
import io.xka.jlite.web.serv.control.http.HttpContentType;
import io.xka.jlite.web.serv.control.http.HttpControl;

import java.time.Duration;
import java.util.concurrent.ArrayBlockingQueue;

public class WebApplication {

    public static ArrayBlockingQueue<HttpControl.SSEEvent> sse;

    public static void main(String[] args) {
        JliteServApp app = JliteServ.options()
                .corsOptions(
                        JliteServ.corsOptions()
                                .enable(true)
                )
                .quick();
        app.run();
        app.ws("/ws", ctl -> {
            ctl.binaryListener(bytes -> {
                if (WebApplication.sse != null) {
                    System.out.println("send sse");
                    try {
                        System.out.println(new String(bytes));
                        WebApplication.sse.put(new HttpControl.SSEEvent("message", bytes));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        });
        app.get("/sse", ctl -> {
            WebApplication.sse = ctl.sse(100, Duration.ofSeconds(1));
            while (true) {

            }
        });
    }
}
