package io.xka.jlite.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import io.xka.jlite.web.options.Options;
import io.xka.jlite.web.options.SSLOptions;
import io.xka.jlite.web.options.ThreadOptions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Jlite {

    public static Options options() {
        return new Options();
    }

    public static SSLOptions sslOptions() {
        return new SSLOptions();
    }


    public static ThreadOptions threadOptions() {
        return new ThreadOptions();
    }

    public static Options yaml(String path) {
        ObjectMapper yamlMapper = new YAMLMapper();
        Options options = null;
        File file = new File(path);
        if (!file.exists()) {
            throw new RuntimeException("yaml file not found: " + path);
        }
        try {
            options = yamlMapper.readValue(Files.readString(file.toPath()), Options.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return options;
    }
}
