package com.ytsummary.infrastructure.youtube;

import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class YouTubeHtmlParser {

    private static final Pattern API_KEY_PATTERN = Pattern.compile("\"INNERTUBE_API_KEY\":\"([^\"]+)\"");

    public String parseApiKey(String html) {
        Matcher matcher = API_KEY_PATTERN.matcher(html);
        if (!matcher.find())
            throw new RuntimeException("INNERTUBE_API_KEY not found");

        return matcher.group(1);
    }

    public String parseVideoId(String ytUrl) {
        URI uri = URI.create(ytUrl);
        String query = uri.getQuery();

        if (query == null)
            throw new RuntimeException("Invalid YouTube URL");

        for (String param : query.split("&")) {
            String[] pair = param.split("=", 2);
            if ("v".equals(pair[0])) {
                return pair[1];
            }
        }

        throw new RuntimeException("Video ID not found");
    }
}
