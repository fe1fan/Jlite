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

    public <T> T get(String url, Map<String, String> headers, Object object, Class<? extends T> type) {
        return null;
    }

    public String get(String url) {
        return this.get(url, null, null, String.class);
    }

    public String get(String url, Map<String, String> headers) {
        return this.get(url, headers, null, String.class);
    }

    public <T> T post(String url, Map<String, String> headers, Object object, Class<? extends T> type) {
        //create json request body
        RequestBody requestBody = RequestBody.create(
                this.jsonAdopter.serialize(object),
                MediaType.parse("application/json")
        );
        Request.Builder requestBuilder = new Request.Builder()
                .url(url);
        if (headers != null) {
            requestBuilder.headers(Headers.of(headers));
        }
        Request request = requestBuilder
                .post(requestBody)
                .build();
        Call call = HttpComponents.getInstance().newCall(request);
        try (Response response = call.execute()) {
            if (HttpStatus.OK_200 != response.code()) {
                throw new RuntimeException("http request failed, status code: " + response.code());
            }
            assert response.body() != null;
            String bodyStr = response.body().string();
            if (type.equals(String.class)) {
                return (T) bodyStr;
            }
            return this.jsonAdopter.deserialize(bodyStr, type);
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }

    public String post(String url, Map<String, String> headers, Object object) {
        return this.get(url, headers, object, String.class);
    }

    public String post(String url, Object object) {
        return this.get(url, null, object, String.class);
    }

    public <T> T post(String url, Object object, Class<? extends T> type) {
        return this.get(url, null, object, type);
    }
}
