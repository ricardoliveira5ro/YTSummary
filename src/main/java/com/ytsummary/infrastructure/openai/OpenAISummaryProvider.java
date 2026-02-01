package com.ytsummary.infrastructure.openai;

import com.ytsummary.domain.model.Transcript;
import com.ytsummary.domain.port.SummaryProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OpenAISummaryProvider implements SummaryProvider {

    private OpenAIClient openAIClient;
    private OpenAIResponseParser openAIResponseParser;

    @Autowired
    public void setOpenAIClient(OpenAIClient openAIClient) {
        this.openAIClient = openAIClient;
    }

    @Autowired
    public void setOpenAIResponseParser(OpenAIResponseParser openAIResponseParser) {
        this.openAIResponseParser = openAIResponseParser;
    }

    @Override
    public String getSummary(Transcript transcript) {
        String response = openAIClient.fetchPromptRequest(transcript);

        return openAIResponseParser.parseOpenAIResponse(response);
    }
}
