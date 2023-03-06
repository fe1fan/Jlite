package io.xka.jlite.web.cli.components;

import io.xka.jlite.web.basic.serializer.JsonAdopter;
import io.xka.jlite.web.cli.options.CliOptions;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Flow;
import java.util.stream.Collectors;

public class HttpV2 {

    protected volatile HttpClient httpClient;

    private final CliOptions cliOptions;

    private final JsonAdopter jsonAdopter;

    public HttpV2(CliOptions cliOptions) {
        this.cliOptions = cliOptions;
        this.jsonAdopter = new JsonAdopter(cliOptions.getSerializer());
    }

    private synchronized HttpClient init() {
        if (httpClient == null) {
            synchronized (this) {
                if (httpClient == null) {
                    httpClient = HttpClient.newBuilder().build();
                }
            }
        }
        return httpClient;
    }

    public HttpClient getHttpClient() {
        if (httpClient == null) {
            return init();
        }
        return httpClient;
    }

    //--------------------------------------- get ----------------------------------------------
    public String get(String url, Map<String, String> headers, Map<String, String> params) {
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("url is null or empty");
        }
        if (params != null && !params.isEmpty()) {
            url += "?" + params.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.joining("&"));
        }
        URI uri;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("url is invalid");
        }
        HttpRequest.Builder getBuilder = HttpRequest.newBuilder()
                .uri(uri)
                .GET();
        if (headers != null && !headers.isEmpty()) {
            getBuilder.headers(headers.entrySet().stream().map(e -> e.getKey() + ":" + e.getValue()).toArray(String[]::new));
        }
        HttpRequest request = getBuilder.build();
        HttpClient httpClient = this.getHttpClient();
        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String get(String url) {
        return get(url, Collections.emptyMap(), Collections.emptyMap());
    }

    public <T> T get(String url, Class<T> clazz) {
        String response = get(url, Collections.emptyMap(), Collections.emptyMap());
        if (response == null || response.isEmpty()) {
            return null;
        }
        return jsonAdopter.deserialize(response, clazz);
    }

    public String get(String url, Map<String, String> params) {
        return get(url, null, params);
    }

    public <T> T get(String url, Map<String, String> params, Class<T> clazz) {
        String response = get(url, null, params);
        if (response == null || response.isEmpty()) {
            return null;
        }
        return jsonAdopter.deserialize(response, clazz);
    }

    public <T> T get(String url, Map<String, String> headers, Map<String, String> params, Class<T> clazz) {
        String response = get(url, headers, params);
        if (response == null || response.isEmpty()) {
            return null;
        }
        return jsonAdopter.deserialize(response, clazz);
    }

    //--------------------------------------- post ----------------------------------------------
    //--------------------------------------- put ----------------------------------------------
    //--------------------------------------- delete ----------------------------------------------
    //--------------------------------------- patch ----------------------------------------------
    //--------------------------------------- head ----------------------------------------------
    //--------------------------------------- options ----------------------------------------------
    //--------------------------------------- trace ----------------------------------------------

    public void sse(String url) {
        HttpClient httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
        HttpRequest request = HttpRequest.newBuilder()
                .GET() // 使用 GET 方法
                .uri(URI.create(url)) // 指定 SSE 服务器的 URL
                .build();

        HttpResponse.BodySubscriber<String> sseSubscriber = new HttpResponse.BodySubscriber<>() {
            private final CompletableFuture<String> cf = new CompletableFuture<>();
            private final StringBuilder sb = new StringBuilder();

            @Override
            public void onSubscribe(Flow.Subscription subscription) {
                subscription.request(Long.MAX_VALUE); // 请求无限流量
            }

            @Override
            public void onNext(List<ByteBuffer> item) {
                for (ByteBuffer bb : item) {
                    System.out.println(StandardCharsets.UTF_8.decode(bb));
                    sb.append(StandardCharsets.UTF_8.decode(bb)); // 将字节缓冲区转换为字符串并追加到 StringBuilder 中
                }
                String s = sb.toString();
                int end;
                while ((end = s.indexOf("\n\n")) >= 0) { // 检查是否有两个换行符，表示一条 SSE 消息结束
                    String msg = s.substring(0, end + 2); // 截取一条 SSE 消息（包含两个换行符）
                    System.out.println(msg); // 打印消息（或者进行其他处理）
                    s = s.substring(end + 2); // 去掉已经处理过的消息部分
                    sb.delete(0, end + 2); // 在 StringBuilder 中也删除已经处理过的消息部分
                }
            }

            @Override
            public void onError(Throwable throwable) {
                cf.completeExceptionally(throwable); // 如果发生错误，完成 CompletableFuture 异常地
            }

            @Override
            public void onComplete() {
                cf.complete(sb.toString()); // 如果完成，返回剩余的字符串（如果有）
            }

            @Override
            public CompletionStage<String> getBody() {
                return cf; // 返回 CompletableFuture 对象作为 Body 的结果类型
            }
        };
        // 使用 HttpClient 发送请求，并使用自定义的 BodySubscriber 接收响应体
        httpClient.sendAsync(request, HttpResponse.BodyHandlers.fromSubscriber(sseSubscriber))
                .thenApply(HttpResponse::body) // 获取响应体结果（这里是一个 CompletableFuture<String> 对象）
                .thenAccept(System.out::println) // 打印最终结果（如果有）
                .join(); // 等待异步操作完成
    }


    public static void main(String[] args) {
        HttpV2 httpV2 = new HttpV2(
                new CliOptions().serializer(JsonAdopter.Engine.JACKSON)
        );
        httpV2.sse("http://127.0.0.1:8080/sse");
    }
}
