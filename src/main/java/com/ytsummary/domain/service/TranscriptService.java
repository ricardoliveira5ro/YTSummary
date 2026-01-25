package com.ytsummary.domain.service;

import com.ytsummary.domain.model.Transcript;
import com.ytsummary.domain.port.TranscriptProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TranscriptService {

    private TranscriptProvider transcriptProvider;

    @Autowired
    public void setTranscriptProvider(TranscriptProvider transcriptProvider) {
        this.transcriptProvider = transcriptProvider;
    }

    public Transcript getTranscript(String ytUrl) {
        return transcriptProvider.getTranscript(ytUrl);
    }
}
