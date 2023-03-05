package io.xka.jlite.web.basic.serializer;

import com.alibaba.fastjson.JSON;

public class FastjsonInternal implements JsonAdopterInternal {

    public FastjsonInternal() {
    }

    @Override
    public String serialize(Object object) {
        return JSON.toJSONString(object);
    }

    @Override
    public <T> T deserialize(String json, Class<T> type) {
        return JSON.parseObject(json, type);
    }
}
