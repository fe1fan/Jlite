package io.xka.jlite.web.serv.control.http;

import java.util.Map;
import java.util.function.Consumer;

public class HttpControls {
    private Consumer<HttpControl> control;

    private Map<String, Object> kvs;

    public Consumer<HttpControl> getControl() {
        return control;
    }

    public void setControl(Consumer<HttpControl> control) {
        this.control = control;
    }

    public Map<String, Object> getKvs() {
        return kvs;
    }

    public void setKvs(Map<String, Object> kvs) {
        this.kvs = kvs;
    }
}
