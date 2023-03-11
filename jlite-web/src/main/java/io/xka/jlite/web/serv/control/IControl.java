package io.xka.jlite.web.serv.control;

import io.xka.jlite.web.basic.runtime.JliteRuntime;
import io.xka.jlite.web.basic.serializer.JsonAdopter;
import io.xka.jlite.web.serv.WorkerExecutors;
import io.xka.jlite.web.serv.options.CORSOptions;
import io.xka.jlite.web.serv.options.ServOptions;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class IControl {

    private final HttpServletRequest httpServletRequest;
    private final HttpServletResponse httpServletResponse;
    private final JsonAdopter jsonAdopter;
    private final ServOptions servOptions;
    Logger logger = LoggerFactory.getLogger(IControl.class);

    protected IControl(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {
        this.httpServletRequest = httpServletRequest;
        this.httpServletResponse = httpServletResponse;

        this.servOptions = JliteRuntime.getServOptions();
        this.jsonAdopter = new JsonAdopter(servOptions.getSerializer());

        CORSOptions corsOptions = servOptions.getCorsOptions();
        if (corsOptions != null && corsOptions.isEnable()) {
            httpServletResponse.setHeader("Access-Control-Allow-Origin", corsOptions.getAllowOrigin());
            httpServletResponse.setHeader("Access-Control-Allow-Methods", corsOptions.getAllowMethods());
            httpServletResponse.setHeader("Access-Control-Allow-Headers", corsOptions.getAllowHeaders());
            httpServletResponse.setHeader("Access-Control-Allow-Credentials", corsOptions.getAllowCredentials());
            httpServletResponse.setHeader("Access-Control-Max-Age", corsOptions.getMaxAge());
        }
    }

    //---------------------------------- request ---------------------------------------//
    public String getQuery(String name) {
        return httpServletRequest.getParameter(name);
    }

    public <T> T getQuery(Class<T> clazz) {
        Map<String, String[]> parameterMap = httpServletRequest.getParameterMap();
        Map<String, Object> convertMap = new HashMap<>(parameterMap.size());
        parameterMap.forEach(
                (key, value) -> {
                    if (value.length < 2) {
                        convertMap.put(key, value[0]);
                    }
                    if (value.length > 1) {
                        convertMap.put(key, Arrays.stream(value).collect(Collectors.toList()));
                    }
                }
        );
        String body = jsonAdopter.serialize(convertMap);
        return jsonAdopter.deserialize(body, clazz);
    }

    public Object getPathVariable(String name) {
        return httpServletRequest.getAttribute(name);
    }

    public byte[] getMultipart(String name) {
        try {
            Part part = httpServletRequest.getPart(name);
            return part.getInputStream().readAllBytes();
        } catch (ServletException | IOException e) {
            logger.error(e.getLocalizedMessage());
            return null;
        }
    }

    public String getHeader(String name) {
        return httpServletRequest.getHeader(name);
    }

    public String getCookie(String name) {
        Cookie[] cookies = httpServletRequest.getCookies();
        if (cookies == null) {
            return null;
        }
        Optional<Cookie> first = Arrays.stream(cookies).filter(cookie -> cookie.getName().equals(name)).findFirst();
        return first.map(Cookie::getValue).orElse(null);
    }

    public String getBody() {
        String body = null;
        try {
            ServletInputStream inputStream = httpServletRequest.getInputStream();
            byte[] bytes = inputStream.readAllBytes();
            body = new String(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return body;
    }

    public <T> T getBodyJson(Class<T> clazz) {
        return jsonAdopter.deserialize(this.getBody(), clazz);
    }

    //---------------------------------- response ---------------------------------------//

    //---------------------------------- text ---------------------------------------//
    public void result(int status, IContentType contentType, Charset charset, Object result) {
        httpServletResponse.setStatus(status);
        httpServletResponse.setContentType(contentType.getName());
        httpServletResponse.setCharacterEncoding(charset.name());
        try (PrintWriter writer = httpServletResponse.getWriter()) {
            writer.println(result);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void result(String result) {
        this.result(HttpStatus.OK_200, IContentType.TEXT_PLAIN, StandardCharsets.UTF_8, result);
    }

    public void result(int status, String result) {
        this.result(status, IContentType.TEXT_PLAIN, StandardCharsets.UTF_8, result);
    }

    public void result(IContentType contentType, String result) {
        this.result(HttpStatus.OK_200, contentType, StandardCharsets.UTF_8, result);
    }

    public void result(int status, IContentType contentType, String result) {
        this.result(status, contentType, StandardCharsets.UTF_8, result);
    }

    public void json(Object object) {
        this.result(
                HttpStatus.OK_200,
                IContentType.APPLICATION_JSON,
                StandardCharsets.UTF_8,
                jsonAdopter.serialize(object));
    }

    public void json(int status, Object object) {
        this.result(status, IContentType.APPLICATION_JSON, StandardCharsets.UTF_8, jsonAdopter.serialize(object));
    }


    //------------------------------- file ------------------------------------------//
    public void file(int status, IContentType contentType, Charset charset, byte[] file) {
        httpServletResponse.setStatus(status);
        httpServletResponse.setContentType(contentType.getName());
        httpServletResponse.setCharacterEncoding(charset.name());
        try (ServletOutputStream outputStream = httpServletResponse.getOutputStream()) {
            outputStream.write(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void file(IContentType contentType, byte[] file) {
        this.file(HttpStatus.OK_200, contentType, StandardCharsets.UTF_8, file);
    }

    public void file(int status, IContentType contentType, byte[] file) {
        this.file(status, contentType, StandardCharsets.UTF_8, file);
    }

    //------------------------------- action ------------------------------------------//
    public void redirect(String url) {
        try {
            httpServletResponse.sendRedirect(url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setCookie(String key, String val) {
        Cookie cookie = new Cookie(key, val);
        httpServletResponse.addCookie(cookie);
    }

    public ArrayBlockingQueue<SSEEvent> sse(int capacity, Duration timeout) {
        ArrayBlockingQueue<SSEEvent> queue = new ArrayBlockingQueue<>(capacity);
        Runnable runnable = () -> {
            httpServletResponse.setContentType(IContentType.TEXT_EVENT_STREAM.getName());
            httpServletResponse.setCharacterEncoding(StandardCharsets.UTF_8.name());
            httpServletResponse.setHeader("Cache-Control", "no-cache");
            httpServletResponse.setHeader("Connection", "keep-alive");
            try (ServletOutputStream outputStream = httpServletResponse.getOutputStream()) {
                while (!Thread.currentThread().isInterrupted()) {
                    SSEEvent sseEvent = queue.poll(timeout.getSeconds(), TimeUnit.SECONDS);
                    if (sseEvent == null) {
                        sseEvent = SSEEvent.ping();
                    }
                    outputStream.print(sseEvent.toString());
                    outputStream.flush();
                }
            } catch (IOException | InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };
        WorkerExecutors.submit(runnable);
        return queue;
    }

    public static class SSEEvent {
        private String event;
        private String data;

        public SSEEvent(String event, String data) {
            this.event = event;
            this.data = data;
        }

        public static SSEEvent ping() {
            return new SSEEvent("ping", "ping");
        }

        public String getEvent() {
            return event;
        }

        public void setEvent(String event) {
            this.event = event;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        @Override
        public String toString() {
            return "event: " + event + "\n" +
                    "data: " + data + "\n\n";
        }
    }
}
