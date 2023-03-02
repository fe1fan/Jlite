package io.xka.jlite.web.runtime;

import io.xka.jlite.web.options.Options;

public class JliteRuntime {

    private static Options options;

    public static Options getOptions() {
        return Options.copy(JliteRuntime.options);
    }

    public static void setOptions(Options options) {
        JliteRuntime.options = options;
    }
}
