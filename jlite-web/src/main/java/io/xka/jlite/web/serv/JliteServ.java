package io.xka.jlite.web.serv;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import io.xka.jlite.web.serv.options.ServOptions;
import io.xka.jlite.web.serv.options.SSLOptions;
import io.xka.jlite.web.serv.options.ThreadOptions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class JliteServ {

    public static ServOptions options() {
        return new ServOptions();
    }

    public static SSLOptions sslOptions() {
        return new SSLOptions();
    }

    public static ThreadOptions threadOptions() {
        return new ThreadOptions();
    }

    public static ServOptions yaml(String path) {
        ObjectMapper yamlMapper = new YAMLMapper();
        ServOptions servOptions = null;
        File file = new File(path);
        if (!file.exists()) {
            throw new RuntimeException("yaml file not found: " + path);
        }
        try {
            servOptions = yamlMapper.readValue(Files.readString(file.toPath()), ServOptions.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return servOptions;
    }
}
