package io.xka.jlite.web.basic.runtime;

import io.xka.jlite.web.cli.options.CliOptions;
import io.xka.jlite.web.serv.options.ServOptions;

public class JliteRuntime {

    private static ServOptions servOptions;

    private static CliOptions cliOptions;

    public static ServOptions getServOptions() {
        return ServOptions.copy(JliteRuntime.servOptions);
    }

    public static void setServOptions(ServOptions servOptions) {
        JliteRuntime.servOptions = servOptions;
    }

    public static CliOptions getCliOptions() {
        return CliOptions.copy(JliteRuntime.cliOptions);
    }

    public static void setCliOptions(CliOptions cliOptions) {
        JliteRuntime.cliOptions = cliOptions;
    }
}
