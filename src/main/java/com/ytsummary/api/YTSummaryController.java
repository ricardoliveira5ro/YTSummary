package com.ytsummary.api;

import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class YTSummaryController {

    @PostMapping("/summarize")
    public ResponseEntity<String> summarize(@RequestParam(name = "yturl") String YTUrl) {
        if (Strings.isBlank(YTUrl))
            return new ResponseEntity<>("Invalid URL", HttpStatus.BAD_REQUEST);

        return null;
    }
}
