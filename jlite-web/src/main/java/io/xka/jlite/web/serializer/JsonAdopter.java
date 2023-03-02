package io.xka.jlite.web.serializer;

public class JsonAdopter {

    private final JsonAdopterInternal jsonAdopterInternal;

    public enum Engine {
        JACKSON,
        GSON,
        FASTJSON
    }

    public static JsonAdopter Default() {
        return new JsonAdopter(Engine.GSON);
    }

    public static JsonAdopter Gson() {
        return new JsonAdopter(Engine.GSON);
    }

    public static JsonAdopter Jackson() {
        return new JsonAdopter(Engine.JACKSON);
    }

    public static JsonAdopter FastJson() {
        return new JsonAdopter(Engine.FASTJSON);
    }

    public JsonAdopter(Engine engine) {
        switch (engine) {
            case JACKSON:
                this.jsonAdopterInternal = new JacksonInternal();
                break;
            case GSON:
                this.jsonAdopterInternal = new GsonInternal();
                break;
            case FASTJSON:
                this.jsonAdopterInternal = new FastjsonInternal();
                break;
            default:
                throw new RuntimeException("Unsupported engine: " + engine);
        }
    }


    //TODO can not serialize inner class
    public String serialize(Object object) {
        return jsonAdopterInternal.serialize(object);
    }

    public <T> T deserialize(String json, Class<T> type) {
        return jsonAdopterInternal.deserialize(json, type);
    }
}

interface JsonAdopterInternal {

    String serialize(Object object);

    <T> T deserialize(String json, Class<T> type);
}
