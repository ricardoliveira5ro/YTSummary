package com.ytsummary.api;

import com.ytsummary.domain.TranscriptService;
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

    @Autowired
    public void setTranscriptService(TranscriptService transcriptService) {
        this.transcriptService = transcriptService;
    }

    @PostMapping("/summarize")
    public ResponseEntity<String> summarize(@RequestParam(name = "ytUrl") String ytUrl, @RequestParam(name = "lang", defaultValue = "en") String language) {
        if (Strings.isBlank(ytUrl))
            return new ResponseEntity<>("Invalid URL", HttpStatus.BAD_REQUEST);

        String transcript = transcriptService.getTranscript(ytUrl, language);

        return ResponseEntity.ok("OK");
    }
}
