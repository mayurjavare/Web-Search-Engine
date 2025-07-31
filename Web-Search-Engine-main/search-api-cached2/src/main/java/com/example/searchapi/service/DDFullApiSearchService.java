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

@Service("ddFullSearchService")
public class DDFullApiSearchService implements ISearchProvider {

	 @Value("${ddg.api.key}")
	    private String ddgApiKey;

	    @Override
	    public List<SearchResult> search(String query) throws Exception {
	        List<SearchResult> results = new ArrayList<>();

	        try {
	            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
	            String url = "https://serpapi.com/search.json?engine=duckduckgo&q=" + encodedQuery + "&kl=us-en&api_key=" + ddgApiKey;

	            HttpRequest request = HttpRequest.newBuilder()
	                    .uri(URI.create(url))
	                    .header("Accept", "application/json")
	                    .GET()
	                    .build();

	            HttpResponse<String> response = HttpClient.newHttpClient()
	                    .send(request, HttpResponse.BodyHandlers.ofString());

	            if (response.statusCode() == 200) {
	                ObjectMapper mapper = new ObjectMapper();
	                JsonNode root = mapper.readTree(response.body());
	                JsonNode organicResults = root.path("organic_results");

	                for (JsonNode item : organicResults) {
	                    SearchResult result = new SearchResult();
	                    result.setTitle(item.path("title").asText("No Title"));
	                    result.setUrl(item.path("link").asText(""));
	                    result.setDescription(item.path("snippet").asText(""));
	                    results.add(result);
	                }
	            } else {
	                System.out.println("❌ DuckDuckGo API error: " + response.statusCode());
	                System.out.println("Response: " + response.body());
	            }

	            System.out.println("✅ DuckDuckGo API hit...");

	        } catch (Exception e) {
	            System.out.println("❌ Error in DuckDuckGoApiSearchService: " + e.getMessage());
	            e.printStackTrace();
	        }

	        return results;
	    }

}
