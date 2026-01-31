package com.ytsummary.infrastructure.openai;

import com.ytsummary.domain.port.SummaryProvider;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class OpenAISummaryProvider implements SummaryProvider {

    private HttpClient httpClient;

    @Value("${openai.api-token}")
    private String openAIApiToken;

    @Value("${openai.api-model}")
    private String openAIApiModel;

    @Autowired
    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public String getSummary(String transcript) {
        JSONObject payload = new JSONObject()
                .put("model", openAIApiModel)
                .put("input", "This is a transcript from a youtube video, I want you to summarize it to understand what they are talking about without having to watch the whole video. " +
                        "Please very concise and include any useful details and return only the summary and in plain text as paragraphs. Here is the transcript: `" + transcript + "`");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/responses"))
                .header(HttpHeaders.CONTENT_TYPE,"application/json")
                .header(HttpHeaders.AUTHORIZATION,"Bearer " + openAIApiToken)
                .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
                .build();

        // To be parsed
        String result = invokeRequest(request);

        JSONObject root = new JSONObject(result);
        JSONObject output = root.getJSONArray("output").getJSONObject(0);

        if (!output.getString("status").equalsIgnoreCase("completed")) {
            throw new RuntimeException("Prompt not completed");
        }

        JSONArray content = output.getJSONArray("content");

        return content.getJSONObject(0).getString("text");
    }

    private String invokeRequest(HttpRequest request) {
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400)
                throw new RuntimeException("Something went wrong");

            return response.body();
        } catch (Exception e) {
            throw new RuntimeException("HTTP call failed", e);
        }
    }
}
