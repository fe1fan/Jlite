package io.xka.jlite.web.cli.components;

import io.xka.jlite.web.basic.serializer.JsonAdopter;
import io.xka.jlite.web.cli.options.CliOptions;
import okhttp3.*;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;
import org.eclipse.jetty.http.HttpStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        this.jsonAdopter = new JsonAdopter(this.options.getSerializer());
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

    private static OkHttpClient copy() {
        return new OkHttpClient.Builder()
                .retryOnConnectionFailure(false)
                .connectionPool(
                        new ConnectionPool(10, 5, TimeUnit.MINUTES)
                )
                .connectTimeout(Duration.ofSeconds(10))
                .readTimeout(Duration.ofSeconds(10))
                .writeTimeout(Duration.ofSeconds(10))
                .build();
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

    public void sse(String url, EventSourceListener eventSourceListener) {
        this.sse(url, null, null, eventSourceListener);
    }


    public void sse(String url, Map<String, String> headers, EventSourceListener eventSourceListener) {
        this.sse(url, headers, null, eventSourceListener);
    }

    public void sse(String url, Map<String, String> headers, Object body, EventSourceListener eventSourceListener) {
        //create json request body
        Request.Builder builder = new Request.Builder().url(url);
        if (headers != null) {
            builder.headers(Headers.of(headers));
        }
        if (body != null) {
            RequestBody requestBody = RequestBody.create(
                    this.jsonAdopter.serialize(body),
                    MediaType.parse("application/json")
            );
            builder.post(requestBody);
        }
        Request request = builder.build();
        EventSources.createFactory(HttpComponents.copy()).newEventSource(request, new EventSourceListener() {
            @Override
            public void onClosed(@NotNull EventSource eventSource) {
                eventSourceListener.onClosed(eventSource);
                HttpComponents.getInstance().dispatcher().executorService().shutdown();
            }

            @Override
            public void onEvent(@NotNull EventSource eventSource, @Nullable String id, @Nullable String type, @NotNull String data) {
                eventSourceListener.onEvent(eventSource, id, type, data);
            }

            @Override
            public void onFailure(@NotNull EventSource eventSource, @Nullable Throwable t, @Nullable Response response) {
                eventSourceListener.onFailure(eventSource, t, response);
            }

            @Override
            public void onOpen(@NotNull EventSource eventSource, @NotNull Response response) {
                eventSourceListener.onOpen(eventSource, response);
            }
        });
    }
}
