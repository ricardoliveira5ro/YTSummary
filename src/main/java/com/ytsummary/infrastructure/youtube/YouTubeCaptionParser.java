package com.ytsummary.infrastructure.youtube;

import com.ytsummary.exception.TranscriptNotFoundException;
import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;

@Component
public class YouTubeCaptionParser {

    private final Logger logger = LoggerFactory.getLogger(YouTubeCaptionParser.class);

    public JSONObject parseTrack(String playerResponse) {
        JSONObject root = new JSONObject(playerResponse);
        JSONArray tracks = root
                .getJSONObject("captions")
                .getJSONObject("playerCaptionsTracklistRenderer")
                .getJSONArray("captionTracks");

        if (tracks == null || tracks.isEmpty())
            throw new TranscriptNotFoundException("No captions found.");

        for (int i = 0; i < tracks.length(); i++) {
            JSONObject track = tracks.getJSONObject(i);
            String language = track.getString("languageCode");

            if ("en".equalsIgnoreCase(language)) {
                logger.info("EN track found");
                return track;
            }
        }

        logger.info("Track auto-detected language found");

        return tracks.getJSONObject(0);
    }

    public String parseCaptionsUrl(JSONObject track) {
        String captionsUrl = track.getString("baseUrl").replaceAll("&fmt=\\w+$", "");

        logger.info("Captions URL {}", captionsUrl);

        return captionsUrl;
    }

    public String parseCaptions(String xml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);

            Document doc = factory.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));

            NodeList nodes = doc.getElementsByTagName("text");
            StringBuilder transcript = new StringBuilder();

            for (int i = 0; i < nodes.getLength(); i++) {
                String text = nodes.item(i).getTextContent().trim();

                if (!text.isEmpty()) {
                    transcript.append(StringEscapeUtils.unescapeHtml4(text)).append(' ');
                }
            }

            logger.info("Transcript parsed successfully");

            return transcript.toString().trim();

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse captions XML", e);
        }
    }
}
