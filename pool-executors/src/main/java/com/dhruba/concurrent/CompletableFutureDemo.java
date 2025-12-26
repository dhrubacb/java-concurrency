package com.dhruba.concurrent;


import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class CompletableFutureDemo {
    private static final HttpClient httpClient = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    private final static List<String> URLS = List.of(
            "https://httpbin.org/delay/4",
            "https://httpbin.org/delay/3",
            "https://httpbin.org/delay/3"
    );

    static void main() {
        long initialMillis = System.currentTimeMillis();

        List<CompletableFuture<String>> completableFutures = fetchParallelly();
        CompletableFuture.allOf(completableFutures.toArray( CompletableFuture[]::new))
                .thenApply(_ ->
                        completableFutures.stream()
                                .map(CompletableFuture::join)
                                .toList()
                ).thenAccept(results ->
                        results.forEach(log::info))
                .join();

        log.info("Total time taken in sec*10: {}", (System.currentTimeMillis() - initialMillis) / 100L);
    }

    private static List<CompletableFuture<String>> fetchParallelly() {
        return URLS.stream()
                .map(url -> httpClient.sendAsync(HttpRequest.newBuilder()
                                        .GET().uri(URI.create(url))
                                        .build(),
                                HttpResponse.BodyHandlers.ofString())
                        .thenApply(HttpResponse::body)
                        .exceptionally(ex -> "Request not successful"))
                .toList();
    }
}
