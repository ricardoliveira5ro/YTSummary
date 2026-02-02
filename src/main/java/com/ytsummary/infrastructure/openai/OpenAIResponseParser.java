package com.ytsummary.infrastructure.openai;

import com.ytsummary.exception.OpenAIException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class OpenAIResponseParser {

    private final Logger logger = LoggerFactory.getLogger(OpenAIResponseParser.class);

    public String parseOpenAIResponse(String response) {
        JSONObject root = new JSONObject(response);
        JSONObject output = root.getJSONArray("output").getJSONObject(0);

        if (!output.getString("status").equalsIgnoreCase("completed")) {
            throw new OpenAIException("Prompt not completed");
        }

        JSONArray content = output.getJSONArray("content");

        logger.info("Prompt successfully completed");

        return content.getJSONObject(0).getString("text");
    }
}
