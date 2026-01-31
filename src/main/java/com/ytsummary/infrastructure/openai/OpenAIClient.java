package com.ytsummary.infrastructure.openai;

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
public class OpenAIClient {

    private HttpClient httpClient;

    @Value("${openai.api-token}")
    private String openAIApiToken;

    @Value("${openai.api-model}")
    private String openAIApiModel;

    @Autowired
    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public String fetchPromptRequest(String transcript) {
        JSONObject payload = new JSONObject()
                .put("model", openAIApiModel)
                .put("input", getPrompt(transcript));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/responses"))
                .header(HttpHeaders.CONTENT_TYPE,"application/json")
                .header(HttpHeaders.AUTHORIZATION,"Bearer " + openAIApiToken)
                .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
                .build();

        return invokeRequest(request);
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

    private String getPrompt(String transcript) {
        return """
            You are an intelligent content analysis system.
            
            TASK:
            Analyze the following YouTube transcript and extract only the information that is important for understanding the video without watching it.
            
            OBJECTIVE:
            Produce a concise, high-signal summary focused on meaning, not narration.
            
            INCLUDE:
            - Core topic(s)
            - Main arguments or ideas
            - Key insights or conclusions
            - Important facts, data, or claims
            - Actionable takeaways (if any)
            - Definitions of critical concepts (if introduced)
            - Decisions, recommendations, or opinions expressed by the speaker
            
            EXCLUDE:
            - Filler words, repetition, greetings, jokes, banter
            - Sponsor messages, ads, promotions
            - Personal anecdotes unless essential to the message
            - Tangents or off-topic discussion
            - Redundant explanations
            
            RULES:
            - Do NOT invent information
            - Do NOT assume missing context
            - Do NOT summarize emotions or tone
            - Do NOT refer to the transcript itself
            - Do NOT include timestamps
            - Do NOT include formatting symbols, bullets, or markdown
            
            OUTPUT FORMAT:
            Plain text only.
            Structured as short, clear paragraphs.
            Each paragraph should represent one main idea.
            Be concise, dense, and information-focused.
            
            TRANSCRIPT:
            """ + transcript;
    }
}
