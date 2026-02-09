package com.ytsummary.domain.port;

import com.ytsummary.domain.model.Transcript;

public interface SummaryProvider {

    String getSummary(Transcript transcript, String context);
}
