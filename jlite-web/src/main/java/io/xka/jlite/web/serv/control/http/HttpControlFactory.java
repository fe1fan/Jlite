package io.xka.jlite.web.serv.control.http;

import org.eclipse.jetty.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class HttpControlFactory {
    final static Logger logger = LoggerFactory.getLogger(HttpControlFactory.class);
    private static final Map<String, Map<HttpMethod, Consumer<HttpControl>>> staticHandlers = new HashMap<>();
    private static final Map<RegexPathEntity, Map<HttpMethod, Consumer<HttpControl>>> regexHandlers = new HashMap<>();
    private static HttpControls uncaptured = null;

    public static void uncaptured(Consumer<HttpControl> handler) {
        HttpControls httpControls = new HttpControls();
        httpControls.setControl(handler);
        httpControls.setKvs(new HashMap<>());
        HttpControlFactory.uncaptured = httpControls;
    }

    public static void register(String path, HttpMethod method, Consumer<HttpControl> handler) {
        if (path == null || path.isEmpty()) {
            throw new RuntimeException("Path cannot be null or empty");
        }
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        Map<HttpMethod, Consumer<HttpControl>> methodHandlers;
        //check path variable
        if (path.contains("{") && path.contains("}")) {
            //check if the path variable is at the end
            if (!path.endsWith("}")) {
                throw new RuntimeException("Path variable must be at the end of the path");
            }
            //check if the path variable is not empty
            if (path.substring(path.lastIndexOf("{") + 1, path.lastIndexOf("}")).isEmpty()) {
                throw new RuntimeException("Path variable cannot be empty");
            }
            //replace the path variable with [a-zA-Z0-9.]+
            String regexPath = path.replaceAll("\\{.*}", "[a-zA-Z0-9.]+");
            //check regex path pattern static path
            Optional<Map.Entry<String, Map<HttpMethod, Consumer<HttpControl>>>> first = staticHandlers
                    .entrySet()
                    .stream()
                    .filter(entry -> entry.getKey().matches(regexPath))
                    .findFirst();
            if (first.isPresent()) {
                logger.warn(
                        "parameter paths are confused with static paths and problems can arise. {} vs {}",
                        path,
                        first.get().getKey());
            }
            RegexPathEntity regexPathEntity = new RegexPathEntity();
            regexPathEntity.setPath(path);
            regexPathEntity.setRegex(regexPath);
            methodHandlers = regexHandlers.computeIfAbsent(regexPathEntity, k -> new HashMap<>());
        } else {
            //check static path pattern regex path
            String finalPath = path;
            Optional<Map.Entry<RegexPathEntity, Map<HttpMethod, Consumer<HttpControl>>>> first =
                    regexHandlers
                            .entrySet()
                            .stream()
                            .filter(entry -> finalPath.matches(entry.getKey().getRegex()))
                            .findFirst();
            first.ifPresent(entry -> {
                logger.warn(
                        "static paths are confused with parameter paths and problems can arise. {} vs {}",
                        finalPath,
                        entry.getKey().getPath());
            });
            //get the method handlers for the path
            methodHandlers = staticHandlers.computeIfAbsent(path, k -> new HashMap<>());
        }
        //check if the method is already registered
        if (methodHandlers.containsKey(method)) {
            throw new RuntimeException("Handler already registered for path: " + path + " and method: " + method);
        }
        methodHandlers.put(method, handler);
    }

    public static HttpControls get(String path, HttpMethod method) {
        if (path.endsWith("/") && path.length() > 1) {
            path = path.substring(0, path.length() - 1);
        }
        Map<HttpMethod, Consumer<HttpControl>> methodHandlers = staticHandlers.get(path);
        if (methodHandlers != null) {
            HttpControls httpControls = new HttpControls();
            httpControls.setControl(methodHandlers.get(method));
            return httpControls;
        }
        for (RegexPathEntity regexPathEntity : regexHandlers.keySet()) {
            if (path.matches(regexPathEntity.getRegex())) {
                HttpControls httpControls = new HttpControls();
                httpControls.setControl(regexHandlers.get(regexPathEntity).get(method));
                httpControls.setKvs(new HashMap<>());
                //get the path variables
                String[] pathVariables = regexPathEntity.getPath().split("/");
                String[] pathValues = path.split("/");
                for (int i = 0; i < pathVariables.length; i++) {
                    if (pathVariables[i].startsWith("{") && pathVariables[i].endsWith("}")) {
                        httpControls
                                .getKvs()
                                .put(pathVariables[i].substring(
                                        pathVariables[i].lastIndexOf("{") + 1,
                                        pathVariables[i].lastIndexOf("}")), pathValues[i]);
                    }
                }
                return httpControls;
            }
        }
        return uncaptured == null ? null : uncaptured;
    }
}

class RegexPathEntity {
    String path;
    String regex;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }
}

