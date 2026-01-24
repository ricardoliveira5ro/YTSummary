package com.ytsummary.infrastructure.youtube;

import com.ytsummary.domain.port.TranscriptProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class YouTubeTranscriptProvider implements TranscriptProvider {

    private YouTubeClient client;
    private YouTubeHtmlParser htmlParser;
    private YouTubeCaptionParser captionParser;

    @Override
    public String getTranscript(String ytUrl, String language) {
        String html = client.fetchVideo(ytUrl);

        String apiKey = htmlParser.parseApiKey(html);
        String videoId = htmlParser.parseVideoId(ytUrl);

        String playerResponse = client.fetchPlayerData(apiKey, videoId);
        String captionsUrl = captionParser.parseCaptionsUrl(playerResponse, language);
        String captionsXml = client.fetchCaptions(captionsUrl);

        return captionParser.parseCaptions(captionsXml);
    }

    @Autowired
    public void setClient(YouTubeClient client) {
        this.client = client;
    }

    @Autowired
    public void setHtmlParser(YouTubeHtmlParser htmlParser) {
        this.htmlParser = htmlParser;
    }

    @Autowired
    public void setCaptionParser(YouTubeCaptionParser captionParser) {
        this.captionParser = captionParser;
    }
}
