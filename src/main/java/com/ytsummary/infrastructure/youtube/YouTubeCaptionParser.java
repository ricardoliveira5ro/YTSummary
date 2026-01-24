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

    public String parseCaptionsUrl(String playerJson, String language) {
        JSONObject root = new JSONObject(playerJson);
        JSONArray tracks = root
                .getJSONObject("captions")
                .getJSONObject("playerCaptionsTracklistRenderer")
                .getJSONArray("captionTracks");

        if (tracks == null || tracks.isEmpty())
            throw new RuntimeException("No captions found.");

        JSONObject selected = null;

        for (int i = 0; i < tracks.length(); i++) {
            JSONObject track = tracks.getJSONObject(i);
            if (language.equals(track.getString("languageCode"))) {
                selected = track;
                break;
            }
        }

        if (selected == null)
            selected = tracks.getJSONObject(0);

        return selected.getString("baseUrl").replaceAll("&fmt=\\w+$", "");
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
