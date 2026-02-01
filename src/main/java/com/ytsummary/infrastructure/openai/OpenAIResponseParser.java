package com.ytsummary.infrastructure.openai;

import com.ytsummary.exception.OpenAIException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class OpenAIResponseParser {

    public String parseOpenAIResponse(String response) {
        JSONObject root = new JSONObject(response);
        JSONObject output = root.getJSONArray("output").getJSONObject(0);

        if (!output.getString("status").equalsIgnoreCase("completed")) {
            throw new OpenAIException("Prompt not completed");
        }

        JSONArray content = output.getJSONArray("content");

        return content.getJSONObject(0).getString("text");
    }
}
