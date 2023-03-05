package io.xka.jlite.web.cli;

import io.xka.jlite.web.basic.runtime.JliteRuntime;
import io.xka.jlite.web.cli.components.HttpComponents;
import io.xka.jlite.web.cli.options.CliOptions;

public class JliteCliApp {

    private HttpComponents httpComponents;

    private final CliOptions options;

    private void init() {
        httpComponents = new HttpComponents(options);
    }


    public JliteCliApp() {
        this.options = JliteRuntime.getCliOptions();
        this.init();
    }

    public HttpComponents http() {
        return httpComponents;
    }
}
