package io.xka.jlite.example;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONPath;
import io.xka.jlite.example.chatgpt.BO;
import io.xka.jlite.example.chatgpt.VO;
import io.xka.jlite.web.basic.serializer.JacksonInternal;
import io.xka.jlite.web.basic.serializer.JsonAdopter;
import io.xka.jlite.web.cli.JliteCli;
import io.xka.jlite.web.cli.JliteCliApp;
import io.xka.jlite.web.serv.JliteServ;
import io.xka.jlite.web.serv.JliteServApp;
import io.xka.jlite.web.serv.control.IControl;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;

public class Application {

    private static final Map<String, VO> cache = new HashMap<>();

    static class Listener extends EventSourceListener {

        private final ArrayBlockingQueue<IControl.SSEEvent> queue;

        public Listener(ArrayBlockingQueue<IControl.SSEEvent> queue) {
            this.queue = queue;
        }
        @Override
        public void onOpen(@NotNull EventSource eventSource, @NotNull Response response) {
            super.onOpen(eventSource, response);
        }

        @Override
        public void onEvent(@NotNull EventSource eventSource, @Nullable String id, @Nullable String type, @NotNull String data) {
            if (data.equals("[DONE]")) {
                onClosed(eventSource);
                return;
            }
            BO bo = JSON.parseObject(data, BO.class);
            BO.ChoicesDTO choicesDTO = bo.getChoices().stream().filter(choice -> choice.getDelta().getContent() != null).findFirst().orElse(null);
            if (choicesDTO == null) {
                return;
            }
            String context = JSONPath.eval(JSON.parseObject(data), "$.choices[0].delta.content").toString();
            IControl.SSEEvent sseEvent = new IControl.SSEEvent("message", context);
            try {
                queue.put(sseEvent);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void onClosed(@NotNull EventSource eventSource) {
            try {
                queue.put(new IControl.SSEEvent("close", "close"));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            super.onClosed(eventSource);
        }
    }

    public static void main(String[] args) {
        //create from options
        JliteServApp app = JliteServ.options()
                .host("0.0.0.0")
                .port(8080)
                .threadOptions(
                        JliteServ.threadOptions()
                                .maxThreads(100)
                                .minThreads(10)
                                .idleTimeout(1000)
                )
                .serializer(JsonAdopter.Engine.JACKSON)
                .quick();
        app.run();
        JliteCliApp quick = JliteCli.options().serializer(JsonAdopter.Engine.JACKSON).quick();
        app.get("/sse", ctl -> {
            String session = ctl.getCookie("_session");
            if (session == null) {
                String replace = UUID.randomUUID().toString().replace("-", "");
                ctl.setCookie("_session", replace);
                session = replace;
            }
            VO orDefault = cache.getOrDefault(session, new VO());
            orDefault.addMessage("user", ctl.getQuery("message"));
            ArrayBlockingQueue<IControl.SSEEvent> queue = ctl.sse(10);
            ArrayBlockingQueue<IControl.SSEEvent> listenerQueue = new ArrayBlockingQueue<>(10);
            Listener listener = new Listener(listenerQueue);
            quick.http().sse(
                    "https://api.openai.com/v1/chat/completions",
                    new HashMap<>() {
                        {
                            put("Authorization", "Bearer sk-xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
                        }
                    },
                    orDefault,
                    listener
            );
            StringBuilder stringBuilder = new StringBuilder();
            while (true) {
                try {
                    IControl.SSEEvent sseEvent = listenerQueue.take();
                    if (sseEvent.getEvent().equals("close")) {
                        break;
                    }
                    stringBuilder.append(sseEvent.getData());
                    queue.put(sseEvent);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            orDefault.addMessage("assistant", stringBuilder.toString());
            cache.put(session, orDefault);
        });
    }
}
