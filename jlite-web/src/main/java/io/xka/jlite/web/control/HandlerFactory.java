package io.xka.jlite.web.control;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class HandlerFactory {
    private static final Map<String, Function<IHandler, Boolean>> handlers = new HashMap<>();

    public static void register(String path, Function<IHandler, Boolean> handler) {
        if (path == null || path.isEmpty()) {
            throw new RuntimeException("Path cannot be null or empty");
        }
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        //check if the path is already registered
        if (handlers.containsKey(path)) {
            throw new RuntimeException("Path already registered: " + path);
        }
        handlers.put(path, handler);
    }

    public static Function<IHandler, Boolean> get(String path) {
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
