package io.xka.jlite.web.serv.control.http;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class HttpHandlerFactory {
    private static final Map<String, Function<HttpHandler, Boolean>> handlers = new HashMap<>();

    public static void register(String path, Function<HttpHandler, Boolean> handler) {
        if (path == null || path.isEmpty()) {
            throw new RuntimeException("path cannot be null or empty");
        }
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        //check if the path is already registered
        if (handlers.containsKey(path)) {
            throw new RuntimeException("path already registered: " + path);
        }
        handlers.put(path, handler);
    }

    public static Function<HttpHandler, Boolean> get(String path) {
        //equals
        Optional<String> first = handlers.keySet().stream().filter(path::equals).findFirst();
        if (first.isPresent()) {
            return handlers.get(first.get());
        }
        //starts with
        first = handlers.keySet().stream().filter("*"::endsWith).filter(path::startsWith).findFirst();
        return first.map(handlers::get).orElse(null);
    }
}
