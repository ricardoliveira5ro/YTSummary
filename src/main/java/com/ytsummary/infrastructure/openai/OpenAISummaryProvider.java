package com.ytsummary.infrastructure.openai;

import com.ytsummary.domain.port.SummaryProvider;

public class OpenAISummaryProvider implements SummaryProvider {

    @Override
    public String getSummary(String transcript) {
        return "";
    }
}
