package com.example.searchapi.service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.searchapi.model.SearchResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service("ddInstantSearchService")
public class DDInstantApiSearchService implements ISearchProvider {

	 @Override
	    public List<SearchResult> search(String query) {
	        List<SearchResult> results = new ArrayList<>();
	        try {
	            String url = "https://api.duckduckgo.com/?q=" +
	                    URLEncoder.encode(query, StandardCharsets.UTF_8) +
	                    "&format=json";

	            HttpClient client = HttpClient.newHttpClient();
	            HttpRequest request = HttpRequest.newBuilder()
	                    .uri(URI.create(url))
	                    .header("Accept", "application/json")
	                    .header("User-Agent", "Mozilla/5.0")
	                    .GET()
	                    .build();

	            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

	            ObjectMapper mapper = new ObjectMapper();
	            JsonNode root = mapper.readTree(response.body());

	            JsonNode relatedTopics = root.path("RelatedTopics");

	            int count = 0;
	            for (JsonNode item : relatedTopics) {
	                if (count >= 2) break;

	                JsonNode topic = item;
	                // Sometimes nested under 'Topics'
	                if (item.has("Topics")) {
	                    topic = item.get("Topics").get(0);
	                }

	                if (topic != null && topic.has("Text")) {
	                    SearchResult r = new SearchResult();
	                    r.setTitle(topic.path("Text").asText("No Title"));
	                    r.setDescription(""); // DuckDuckGo doesn’t give separate description
	                    r.setUrl(topic.path("FirstURL").asText(""));
	                    results.add(r);
	                    count++;
	                }
	            }
	            System.out.println("DD_Hit");

	        } catch (Exception e) {
	            System.out.println("Error in DuckDuckGoSearchService: " + e.getMessage());
	            e.printStackTrace();
	        }
	        return results;
	    }

}
