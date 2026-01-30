package com.ytsummary.domain.service;

import com.ytsummary.domain.port.SummaryProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SummaryService {

    private SummaryProvider summaryProvider;

    @Autowired
    public void setSummaryProvider(SummaryProvider summaryProvider) {
        this.summaryProvider = summaryProvider;
    }

    public String getSummary(String transcript) {
        return summaryProvider.getSummary(transcript);
    }
}
