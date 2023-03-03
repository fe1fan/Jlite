package io.xka.jlite.web.cli.options;

import io.xka.jlite.web.basic.runtime.JliteRuntime;
import io.xka.jlite.web.basic.serializer.JsonAdopter;
import io.xka.jlite.web.cli.JliteCliApp;
import io.xka.jlite.web.serv.JliteServApp;

public class CliOptions {

    private JsonAdopter.Engine serializer = JsonAdopter.Engine.JACKSON;

    public static CliOptions create() {
        return new CliOptions();
    }

    public JliteCliApp quick() {
        JliteRuntime.setCliOptions(this);
        return new JliteCliApp();
    }

    public static CliOptions copy(CliOptions cliOptions) {
        return CliOptions.create()
                .serializer(cliOptions.serializer);
    }

    /**
     * ---------------- settings ----------------
     */
    public CliOptions serializer(JsonAdopter.Engine serializer) {
        this.serializer = serializer;
        return this;
    }

    /**
     * ---------------- getters ----------------
     */
    public JsonAdopter.Engine serializer() {
        return serializer;
    }
}
