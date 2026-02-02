package com.ytsummary.infrastructure.openai;

import com.ytsummary.domain.model.Transcript;
import com.ytsummary.exception.OpenAIException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final Logger logger = LoggerFactory.getLogger(OpenAIClient.class);

    @Autowired
    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public String fetchPromptRequest(Transcript transcript) {
        logger.info("Fetching AI model response");

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
            logger.info("Invoking request {}", request.uri().toString());

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400)
                throw new OpenAIException("YouTube request failed: " + response.statusCode());

            return response.body();
        } catch (Exception e) {
            throw new RuntimeException("HTTP call failed", e);
        }
    }

    private String getPrompt(Transcript transcript) {
        return """
            You are an intelligent content analysis system.
            
            LANGUAGE CONTEXT:
            - The transcript language is: %s.
            - Analyze and understand the transcript in its original language.
            - Do NOT translate during analysis.
            - The final summary MUST be written in English.
            
            QUALITY SAFEGUARDS:
            - If technical terms, code, product names, or proper nouns appear in another language, preserve them as-is.
            - If parts of the transcript are unclear, corrupted, or incomplete, ignore them rather than guessing or inventing content.
            
            TASK:
            Analyze the following YouTube transcript and extract only the information that is important for understanding the video without watching it.
            
            OBJECTIVE:
            Produce a concise, high-signal English summary focused on meaning, not narration.
            
            INCLUDE IN SUMMARY:
            - Core topic(s)
            - Main arguments or ideas
            - Key insights or conclusions
            - Important facts, data, or claims
            - Actionable takeaways (if any)
            - Definitions of critical concepts (if introduced)
            - Decisions, recommendations, or opinions expressed by the speaker
            
            KEYWORDS REQUIREMENTS:
            - Extract 5–12 keywords or short key phrases.
            - Keywords must represent the most important concepts, technologies, methods, or themes.
            - Prefer specific and meaningful terms over generic ones.
            - Preserve original-language technical terms, product names, or proper nouns.
            - Avoid duplicates, filler terms, or overly broad words.
            
            EXCLUDE:
            - Filler words, repetition, greetings, jokes, banter
            - Sponsor messages, ads, promotions
            - Personal anecdotes unless essential to the message
            - Tangents or off-topic discussion
            - Redundant explanations
            
            RULES:
            - Do NOT invent information
            - Do NOT assume missing context
            - Do NOT refer to the transcript itself
            - Do NOT use "The transcript"
            - Do NOT include timestamps
            - Do NOT include formatting symbols, bullets, or markdown
            
            OUTPUT FORMAT:
            Plain text only.
            
            SUMMARY REQUIREMENTS:
            - Structured as short, clear paragraphs.
            - Each paragraph should represent one main idea.
            - Be concise, dense, and information-focused.
            
            Then, provide the keywords:
            - On a new line, start with: Keywords:
            - List the keywords separated by commas
            
            TRANSCRIPT:
            %s
            """.formatted(transcript.language(), transcript.content());
    }
}
