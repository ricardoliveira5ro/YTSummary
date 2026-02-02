package com.ytsummary.infrastructure.youtube;

import com.ytsummary.exception.YoutubeException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class YouTubeClient {

    private HttpClient httpClient;
    private final Logger logger = LoggerFactory.getLogger(YouTubeClient.class);

    @Autowired
    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public String fetchVideo(String ytUrl) {
        logger.info("Fetching video");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ytUrl))
                .GET()
                .build();

        return invokeRequest(request);
    }

    public String fetchPlayerData(String apiKey, String videoId) {
        logger.info("Fetching player data");

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
        logger.info("Fetching captions");

         HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(captionsUrl))
                .GET()
                .build();

         return invokeRequest(request);
    }

    private String invokeRequest(HttpRequest request) {
        try {
            logger.info("Invoking request {}", request.uri().toString());

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400)
                throw new YoutubeException("YouTube request failed: " + response.statusCode());

            return response.body();
        } catch (Exception e) {
            throw new RuntimeException("HTTP call failed", e);
        }
    }
}
