package com.ytsummary.infrastructure.youtube;

import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;

@Component
public class YouTubeCaptionParser {

    public JSONObject extractTrack(String playerResponse) {
        JSONObject root = new JSONObject(playerResponse);
        JSONArray tracks = root
                .getJSONObject("captions")
                .getJSONObject("playerCaptionsTracklistRenderer")
                .getJSONArray("captionTracks");

        if (tracks == null || tracks.isEmpty())
            throw new RuntimeException("No captions found.");

        for (int i = 0; i < tracks.length(); i++) {
            JSONObject track = tracks.getJSONObject(i);
            if ("en".equals(track.getString("languageCode"))) {
                return track;
            }
        }

        return tracks.getJSONObject(0);
    }

    public String parseCaptionsUrl(JSONObject track) {
        return track.getString("baseUrl").replaceAll("&fmt=\\w+$", "");
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

            return transcript.toString().trim();

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse captions XML", e);
        }
    }
}
