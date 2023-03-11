package io.xka.jlite.web.serv.control.ws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class WSControlFactory {

    private static Logger logger = LoggerFactory.getLogger(WSControlFactory.class);

    private final static Map<String, Consumer<WSControl>> registry = new HashMap<>();

    public static void register(String path, Consumer<WSControl> handler) {
        if (path == null || path.isEmpty()) {
            throw new RuntimeException("Path cannot be null or empty");
        }
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        logger.info("register ws path: {}", path);
        if (registry.containsKey(path)) {
            throw new RuntimeException("path already registered");
        }
        registry.put(path, handler);
    }

    public static Consumer<WSControl> get(String path) {
        logger.info("get ws path: {}", path);
        return registry.get(path);
    }
}
