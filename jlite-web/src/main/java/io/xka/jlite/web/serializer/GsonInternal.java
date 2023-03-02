package io.xka.jlite.web.serializer;

import com.google.gson.Gson;

public class GsonInternal implements JsonAdopterInternal {

    private final Gson gson;

    public GsonInternal() {
        gson = new Gson();
    }

    @Override
    public String serialize(Object object) {
        return gson.toJson(object);
    }

    @Override
    public <T> T deserialize(String json, Class<T> type) {
        return gson.fromJson(json, type);
    }
}
