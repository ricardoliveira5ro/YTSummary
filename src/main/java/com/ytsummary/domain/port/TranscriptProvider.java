package com.ytsummary.domain.port;

import com.ytsummary.domain.model.Transcript;

public interface TranscriptProvider {

    Transcript getTranscript(String ytUrl);
}
