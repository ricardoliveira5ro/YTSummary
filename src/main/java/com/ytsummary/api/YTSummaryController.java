package com.ytsummary.api;

import com.ytsummary.domain.model.Transcript;
import com.ytsummary.domain.service.SummaryService;
import com.ytsummary.domain.service.TranscriptService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class YTSummaryController {

    private TranscriptService transcriptService;
    private SummaryService summaryService;

    @Autowired
    public void setTranscriptService(TranscriptService transcriptService) {
        this.transcriptService = transcriptService;
    }

    @Autowired
    public void setSummaryService(SummaryService summaryService) {
        this.summaryService = summaryService;
    }

    @PostMapping("/summarize")
    public ResponseEntity<String> summarize(@RequestParam(name = "ytUrl") String ytUrl) {
        if (Strings.isBlank(ytUrl))
            return new ResponseEntity<>("Invalid URL", HttpStatus.BAD_REQUEST);

        Transcript transcript = transcriptService.getTranscript(ytUrl);

        String summary = summaryService.getSummary(transcript.content());

        return ResponseEntity.ok(summary);
    }
}
