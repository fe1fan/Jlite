package io.xka.jlite.web.serv.control;

import io.xka.jlite.web.basic.runtime.JliteRuntime;
import io.xka.jlite.web.basic.serializer.JsonAdopter;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.stream.Collectors;

public class IControl {

    Logger logger = LoggerFactory.getLogger(IControl.class);

    private final HttpServletRequest httpServletRequest;

    private final HttpServletResponse httpServletResponse;
    
    private final JsonAdopter jsonAdopter;

    private final ServOptions servOptions;

    private PrintWriter sseWriter;


    protected IControl(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
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
        Optional<Cookie> first = Arrays.stream(cookies).filter(cookie -> cookie.getName().equals(name)).findFirst();
        return first.map(Cookie::getValue).orElse(null);
    }

    public <T> T fromJson(Class<T> clazz) {
        String body = null;
        try {
            ServletInputStream inputStream = httpServletRequest.getInputStream();
            byte[] bytes = inputStream.readAllBytes();
            body = new String(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return jsonAdopter.deserialize(body, clazz);
    }

    public void result(String result) {
        httpServletResponse.setStatus(HttpStatus.OK_200);
        httpServletResponse.setContentType("text/plain");
        httpServletResponse.setCharacterEncoding("UTF-8");
        try (PrintWriter writer = httpServletResponse.getWriter()) {
            writer.println(result);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void result(int status, String result) {
        httpServletResponse.setStatus(status);
        httpServletResponse.setContentType("text/plain");
        httpServletResponse.setCharacterEncoding("UTF-8");
        try (PrintWriter writer = httpServletResponse.getWriter()) {
            writer.println(result);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void json(Object object) {
        httpServletResponse.setStatus(HttpStatus.OK_200);
        httpServletResponse.setContentType("application/json");
        httpServletResponse.setCharacterEncoding("UTF-8");
        try (PrintWriter writer = httpServletResponse.getWriter()) {
            writer.println(jsonAdopter.serialize(object));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void json(int status, Object object) {
        httpServletResponse.setStatus(status);
        httpServletResponse.setContentType("application/json");
        httpServletResponse.setCharacterEncoding("UTF-8");
        try (PrintWriter writer = httpServletResponse.getWriter()) {
            writer.println(jsonAdopter.serialize(object));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void file(String contentType, byte[] file) {
        httpServletResponse.setStatus(HttpStatus.OK_200);
        httpServletResponse.setContentType(contentType);
        httpServletResponse.setCharacterEncoding("UTF-8");
        try(ServletOutputStream outputStream = httpServletResponse.getOutputStream()){
            outputStream.write(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void file(int status, String contentType, byte[] file) {
        httpServletResponse.setStatus(status);
        httpServletResponse.setContentType(contentType);
        httpServletResponse.setCharacterEncoding("UTF-8");
        try(ServletOutputStream outputStream = httpServletResponse.getOutputStream()){
            outputStream.write(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void redirect(String url) {
        try {
            httpServletResponse.sendRedirect(url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sse(ArrayBlockingQueue<Map<String, String>> seeQueue) {
        httpServletResponse.setStatus(HttpStatus.OK_200);
        httpServletResponse.setContentType("text/event-stream");
        httpServletResponse.setCharacterEncoding("UTF-8");
        try {
            sseWriter = httpServletResponse.getWriter();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        new Thread(() -> {
            while (true) {
                try {
                    Map<String, String> event = seeQueue.take();
                    sseWriter.println("event: " + event.get("event"));
                    sseWriter.println("data: " + event.get("data"));
                    sseWriter.println();
                    sseWriter.flush();
                } catch (InterruptedException e) {
                    logger.error(e.getLocalizedMessage());
                }
            }
        }).start();
    }
}
