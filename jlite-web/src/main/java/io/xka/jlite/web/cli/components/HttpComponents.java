package io.xka.jlite.web.cli.components;

import io.xka.jlite.web.basic.serializer.JsonAdopter;
import io.xka.jlite.web.cli.options.CliOptions;
import okhttp3.*;
import org.eclipse.jetty.http.HttpStatus;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HttpComponents {

    private static volatile OkHttpClient okHttpClient = null;

    private final CliOptions options;

    private final JsonAdopter jsonAdopter;

    public HttpComponents(CliOptions options) {
        this.options = options;
        this.jsonAdopter = new JsonAdopter(this.options.serializer());
    }

    private static OkHttpClient getInstance() {
        if (okHttpClient == null) {
            synchronized (HttpComponents.class) {
                if (okHttpClient == null) {
                    okHttpClient = new OkHttpClient.Builder()
                            .retryOnConnectionFailure(false)
                            .connectionPool(
                                    new ConnectionPool(10, 5, TimeUnit.MINUTES)
                            )
                            .connectTimeout(Duration.ofSeconds(10))
                            .readTimeout(Duration.ofSeconds(10))
                            .writeTimeout(Duration.ofSeconds(10))
                            .build();
                }
            }
        }
        return okHttpClient;
    }

    // simple get
    public String get(String url) {
        return null;
    }

    // simple post
    public <T> T postJson(String url, Map<String, String> headers, Object body, Class<? extends T> type) {
        //create json request body
        RequestBody requestBody = RequestBody.create(
                this.jsonAdopter.serialize(body),
                MediaType.parse("application/json")
        );
        Request request = new Request.Builder()
                .url(url)
                .headers(Headers.of(headers))
                .post(requestBody)
                .build();
        Call call = HttpComponents.getInstance().newCall(request);
        try (Response response = call.execute()) {
            if (HttpStatus.OK_200 != response.code()) {
                throw new RuntimeException("http request failed, status code: " + response.code());
            }
            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                throw new RuntimeException("http request failed, response body is null");
            }
            String bodyStr = responseBody.string();
            if (type.equals(String.class)) {
                return (T) bodyStr;
            }
            return this.jsonAdopter.deserialize(responseBody.string(), type);
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }
}
