package com.ytsummary.domain.service;

import com.ytsummary.domain.model.Language;
import com.ytsummary.domain.model.Transcript;
import com.ytsummary.domain.port.TranscriptProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TranscriptService {

    private TranscriptProvider transcriptProvider;

    @Autowired
    public void setTranscriptProvider(TranscriptProvider transcriptProvider) {
        this.transcriptProvider = transcriptProvider;
    }

    public String getTranscript(String ytUrl, String language) {
        return transcriptProvider.getTranscript(ytUrl, language);
    }

    // To be removed
    public Transcript getTranscriptDeprecated(String ytUrl, String language) {
        try {
            URI ytUri = URI.create(ytUrl);
            String query = ytUri.getQuery();
            String videoId = null;

            for (String param : query.split("&")) {
                String[] pair = param.split("=", 2);
                if ("v".equals(pair[0])) {
                    videoId = pair[1];
                    break;
                }
            }

            HttpClient client = HttpClient.newHttpClient();

            HttpRequest videoReq = HttpRequest.newBuilder()
                    .uri(ytUri)
                    .GET()
                    .build();

            String html = client.send(videoReq, HttpResponse.BodyHandlers.ofString()).body();

            Pattern pattern = Pattern.compile("\"INNERTUBE_API_KEY\":\"([^\"]+)\"");
            Matcher matcher = pattern.matcher(html);

            if (!matcher.find())
                throw new RuntimeException("INNERTUBE_API_KEY not found.");

            String apiKey = matcher.group(1);

            JSONObject postData = new JSONObject();
            JSONObject context = new JSONObject();
            JSONObject clientInfo = new JSONObject();

            clientInfo.put("clientName", "ANDROID");
            clientInfo.put("clientVersion", "20.10.38");
            context.put("client", clientInfo);
            postData.put("context", context);
            postData.put("videoId", videoId);

            HttpRequest transcriptReq = HttpRequest.newBuilder()
                    .uri(URI.create("https://www.youtube.com/youtubei/v1/player?key=" + apiKey))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(postData.toString()))
                    .build();

            String response = client.send(transcriptReq, HttpResponse.BodyHandlers.ofString()).body();

            JSONObject jsonResponse = new JSONObject(response);
            JSONArray tracks = jsonResponse.getJSONObject("captions")
                    .getJSONObject("playerCaptionsTracklistRenderer")
                    .getJSONArray("captionTracks");

            if (tracks == null || tracks.isEmpty())
                throw new RuntimeException("No captions found.");

            List<JSONObject> filteredTracks = new ArrayList<>();
            Language trackLanguage = Language.EN;

            for (int i = 0; i < tracks.length(); i++) {
                JSONObject track = tracks.getJSONObject(i);

                if (language.equals(track.getString("languageCode"))) {
                    filteredTracks.add(track);
                    trackLanguage = Language.valueOf(track.getString("languageCode").toUpperCase());
                    break;
                }
            }

            JSONObject track = !filteredTracks.isEmpty() ?
                    filteredTracks.getFirst() : tracks.getJSONObject(0);

            String trackUrl = track.getString("baseUrl").replaceAll("&fmt=\\w+$", "");

            HttpRequest captionsReq = HttpRequest.newBuilder()
                    .uri(URI.create(trackUrl))
                    .GET()
                    .build();

            String xml = client.send(captionsReq, HttpResponse.BodyHandlers.ofString()).body();

            StringBuilder transcript = new StringBuilder();

            Document doc = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(new InputSource(new StringReader(xml)));

            NodeList texts = doc.getElementsByTagName("text");

            for (int i = 0; i < texts.getLength(); i++) {
                String caption = texts.item(i).getTextContent().trim();

                if (!caption.isEmpty()) {
                    transcript.append(caption).append(' ');
                }
            }

            return new Transcript(transcript.toString(), trackLanguage);

        } catch (IOException | InterruptedException | ParserConfigurationException | SAXException e) {
            throw new RuntimeException(e);
        }
    }
}
