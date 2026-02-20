package com.nic7.bot.hypixel;

import net.hypixel.api.HypixelAPI;
import net.hypixel.api.http.HypixelHttpClient;
import net.hypixel.api.http.HypixelHttpResponse;
import net.hypixel.api.reply.PlayerReply;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class GetInfo {
    public static HypixelAPI API;
    // Standard Java HTTP Client
    private static final HttpClient client = HttpClient.newHttpClient();

    public void initAPIKey(String key) {
        UUID apiKey = UUID.fromString(key);

        // We manually implement the missing methods here
        API = new HypixelAPI(new HypixelHttpClient() {
            @Override
            public CompletableFuture<HypixelHttpResponse> makeAuthenticatedRequest(String url) {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("API-Key", apiKey.toString()) // Authenticates your key
                        .build();

                return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                        .thenApply(res -> new HypixelHttpResponse(res.statusCode(), res.body()));
            }

            @Override
            public CompletableFuture<HypixelHttpResponse> makeRequest(String url) {
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
                return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                        .thenApply(res -> new HypixelHttpResponse(res.statusCode(), res.body()));
            }

            @Override
            public void shutdown() {
                // Java's HttpClient doesn't require a specific shutdown call here
            }
        });
    }

    public void test(String username) {

        java.util.UUID uuid = java.util.UUID.fromString(username);
    }
}