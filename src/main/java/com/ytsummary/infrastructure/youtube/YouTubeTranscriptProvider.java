package com.ytsummary.infrastructure.youtube;

import com.ytsummary.domain.model.Transcript;
import com.ytsummary.domain.port.TranscriptProvider;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class YouTubeTranscriptProvider implements TranscriptProvider {

    private YouTubeClient client;
    private YouTubeHtmlParser htmlParser;
    private YouTubeCaptionParser captionParser;

    @Override
    public Transcript getTranscript(String ytUrl) {
        String html = client.fetchVideo(ytUrl);

        String apiKey = htmlParser.parseApiKey(html);
        String videoId = htmlParser.parseVideoId(ytUrl);

        String playerResponse = client.fetchPlayerData(apiKey, videoId);

        JSONObject track = captionParser.parseTrack(playerResponse);
        String captionsUrl = captionParser.parseCaptionsUrl(track);
        String captionsXml = client.fetchCaptions(captionsUrl);
        String captions = captionParser.parseCaptions(captionsXml);

        return new Transcript(captions, track.getString("languageCode"));
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
