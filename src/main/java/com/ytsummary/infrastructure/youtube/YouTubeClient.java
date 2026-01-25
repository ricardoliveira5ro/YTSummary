package com.ytsummary.infrastructure.youtube;

import com.ytsummary.exception.YoutubeException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class YouTubeClient {

    private HttpClient httpClient;

    @Autowired
    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public String fetchVideo(String ytUrl) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ytUrl))
                .GET()
                .build();

        return invokeRequest(request);
    }

    public String fetchPlayerData(String apiKey, String videoId) {
        JSONObject payload = new JSONObject()
                .put("context", new JSONObject()
                        .put("client", new JSONObject()
                                .put("clientName", "ANDROID")
                                .put("clientVersion", "20.10.38")))
                .put("videoId", videoId);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://www.youtube.com/youtubei/v1/player?key=" + apiKey))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
                .build();

        return invokeRequest(request);
    }

    public String fetchCaptions(String captionsUrl) {
         HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(captionsUrl))
                .GET()
                .build();

         return invokeRequest(request);
    }

    private String invokeRequest(HttpRequest request) {
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400)
                throw new YoutubeException("YouTube request failed: " + response.statusCode());

            return response.body();
        } catch (Exception e) {
            throw new RuntimeException("HTTP call failed", e);
        }
    }
}
