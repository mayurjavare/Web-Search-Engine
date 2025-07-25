package com.example.searchapi.service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.searchapi.model.SearchResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service("googleSearchService")
public class GoogleSerachService implements ISearchProvider {
	
	@Value("${google.api.key}")
    private String googleApiKey;

    @Value("${google.search.cx}")
    private String googleCx;

    private static final String GOOGLE_URL = "https://www.googleapis.com/customsearch/v1";
	
	

	@Override
	public List<SearchResult> search(String query) throws Exception {
		List<SearchResult> results = new ArrayList<>();
        String encoded = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String url = GOOGLE_URL + "?q=" + encoded + "&key=" + googleApiKey + "&cx=" + googleCx;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JsonNode json = new ObjectMapper().readTree(response.body());
            JsonNode items = json.get("items");
            if (items != null) {
                for (JsonNode item : items) {
                    SearchResult r = new SearchResult();
                    r.setTitle(item.has("title") ? item.get("title").asText() : "No Title");
                    r.setUrl(item.has("link") ? item.get("link").asText() : "");
                    r.setDescription(item.has("snippet") ? item.get("snippet").asText() : "");
                    results.add(r);
                }
            }
            System.out.println("Google Hit...");
        } else {
            System.out.println("Google API error: " + response.statusCode());
        }

        return results;
    }

}
