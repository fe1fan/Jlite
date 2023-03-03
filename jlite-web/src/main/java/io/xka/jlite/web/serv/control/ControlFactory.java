package io.xka.jlite.web.serv.control;

import org.eclipse.jetty.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class ControlFactory {
    private static final Map<String, Map<HttpMethod, Consumer<IControl>>> staticHandlers = new HashMap<>();

    private static final Map<RegexPathEntity, Map<HttpMethod, Consumer<IControl>>> regexHandlers = new HashMap<>();

    final static Logger logger = LoggerFactory.getLogger(ControlFactory.class);

    public static void register(String path, HttpMethod method, Consumer<IControl> handler) {
        if (path == null || path.isEmpty()) {
            throw new RuntimeException("Path cannot be null or empty");
        }
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        Map<HttpMethod, Consumer<IControl>> methodHandlers;
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
            Optional<Map.Entry<String, Map<HttpMethod, Consumer<IControl>>>> first = staticHandlers.entrySet().stream().filter(entry -> entry.getKey().matches(regexPath)).findFirst();
            if (first.isPresent()) {
                logger.warn("parameter paths are confused with static paths and problems can arise. {} vs {}", path, first.get().getKey());
            }
            RegexPathEntity regexPathEntity = new RegexPathEntity();
            regexPathEntity.setPath(path);
            regexPathEntity.setRegex(regexPath);
            methodHandlers = regexHandlers.computeIfAbsent(regexPathEntity, k -> new HashMap<>());
        } else {
            //check static path pattern regex path
            String finalPath = path;
            Optional<Map.Entry<RegexPathEntity, Map<HttpMethod, Consumer<IControl>>>> first =
                    regexHandlers.entrySet().stream().filter(entry -> finalPath.matches(entry.getKey().getRegex())).findFirst();
            first.ifPresent(entry -> {
                logger.warn("static paths are confused with parameter paths and problems can arise. {} vs {}", finalPath, entry.getKey().getPath());
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

    public static Controls get(String path, HttpMethod method) {
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        Map<HttpMethod, Consumer<IControl>> methodHandlers = staticHandlers.get(path);
        if (methodHandlers != null) {
            Controls controls = new Controls();
            controls.setControl(methodHandlers.get(method));
            return controls;
        }
        for (RegexPathEntity regexPathEntity : regexHandlers.keySet()) {
            if (path.matches(regexPathEntity.getRegex())) {
                Controls controls = new Controls();
                controls.setControl(regexHandlers.get(regexPathEntity).get(method));
                controls.setKvs(new HashMap<>());
                //get the path variables
                String[] pathVariables = regexPathEntity.getPath().split("/");
                String[] pathValues = path.split("/");
                for (int i = 0; i < pathVariables.length; i++) {
                    if (pathVariables[i].startsWith("{") && pathVariables[i].endsWith("}")) {
                        controls.getKvs().put(pathVariables[i].substring(pathVariables[i].lastIndexOf("{") + 1, pathVariables[i].lastIndexOf("}")), pathValues[i]);
                    }
                }
                return controls;
            }
        }

        return null;
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

class Controls {
    private Consumer<IControl> control;

    private Map<String, Object> kvs;

    public Consumer<IControl> getControl() {
        return control;
    }

    public void setControl(Consumer<IControl> control) {
        this.control = control;
    }

    public Map<String, Object> getKvs() {
        return kvs;
    }

    public void setKvs(Map<String, Object> kvs) {
        this.kvs = kvs;
    }
}
