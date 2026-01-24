package com.ytsummary.domain.port;

public interface TranscriptProvider {

    String getTranscript(String ytUrl, String language);
}
