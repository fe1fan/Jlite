package io.xka.jlite.example;

import io.xka.jlite.web.basic.serializer.JsonAdopter;
import io.xka.jlite.web.serv.JliteServ;
import io.xka.jlite.web.serv.JliteServApp;
import io.xka.jlite.web.serv.control.IContentType;
import io.xka.jlite.web.serv.control.IControl;

import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;

public class WebApplication {

    public static void main(String[] args) {
        JliteServApp app = JliteServ.options().quick();
        app.run();
        app.uncaptured(ctl -> {
            ctl.result(IContentType.TEXT_HTML, "<h1>404 Not Found </h1>");
        });
        app.get("/", ctl -> {
            ctl.result("Hello World");
        });
        app.get("/json", ctl -> {
            ctl.json(new HashMap<>() {
                {
                    put("name", "xka");
                    put("age", 1);
                }
            });
        });
        app.get("/sse", ctl -> {
            ArrayBlockingQueue<IControl.SSEEvent> sse = ctl.sse(10);
            for (int i = 0; i < 10; i++) {
                sse.add(new IControl.SSEEvent("message", "hello world"));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
