package com.example.searchapi.service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.example.searchapi.controller.SearchController;
import com.example.searchapi.model.SearchResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service("serpApiSearchService")
public class SerpApiSearchService implements ISearchProvider {
	
	@Value("${serpapi.api.key}")
    private String apiKey;
	
//    private final SearchController searchController;

	

    private static final String SEARCH_URL = "https://serpapi.com/search";

//    SerpApiSearchService(SearchController searchController) {
//        this.searchController = searchController;
//    }

    @Override
    public List<SearchResult> search(String query) throws Exception {
        List<SearchResult> results = new ArrayList<>();
        String encoded = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String url = SEARCH_URL + "?q=" + encoded + "&engine=google&api_key=" + apiKey;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JsonNode json = new ObjectMapper().readTree(response.body());
            JsonNode items = json.get("organic_results");
            if (items != null) {
                for (JsonNode item : items) {
                    SearchResult r = new SearchResult();
                    r.setTitle(item.has("title") ? item.get("title").asText() : "No Title");
                    r.setUrl(item.has("link") ? item.get("link").asText() : "");
                    r.setDescription(item.has("snippet") ? item.get("snippet").asText() : "");
                    results.add(r);
                }
            }
            System.out.println("Serp Hit...");
        } else {
            System.out.println("SerpAPI error: " + response.statusCode());
        }

        return results;
    }

}
