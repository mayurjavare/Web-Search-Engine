package com.example.searchapi.service;

import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.searchapi.model.SearchResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service("braveSearchService")
public class BraveApiSearchService implements ISearchProvider {

    @Value("${brave.api.key}")
    private String braveApiKey;

    @Value("${brave.api.url}")
    private String braveApiUrl;

    @Override
    public List<SearchResult> search(String query) throws Exception {
        List<SearchResult> results = new ArrayList<>();
        try {
//        	
//            String url = braveApiUrl + "?q=" + URI.create(query).toASCIIString();
        	String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String url = braveApiUrl + "?q=" + encodedQuery;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Accept", "application/json")
                    .header("Accept-Encoding", "gzip")
                    .header("X-Subscription-Token", braveApiKey)
                    .GET()
                    .build();

            HttpResponse<InputStream> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofInputStream());

            if (response.statusCode() == 200) {
                InputStream gzipStream = new GZIPInputStream(response.body());
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(gzipStream);

                JsonNode webResults = root.path("web").path("results");
                for (JsonNode item : webResults) {
                    SearchResult r = new SearchResult();
                    r.setTitle(item.path("title").asText("No Title"));
                    r.setUrl(item.path("url").asText(""));
                    r.setDescription(item.path("description").asText(""));
                    results.add(r);
                }
            } else {
                System.out.println("Brave API error: " + response.statusCode());
                System.out.println(new String(response.body().readAllBytes())); // debug only
            }
            System.out.println("Brave hit...");

        } catch (Exception e) {
            System.out.println("Error in BraveApiSearchService: " + e.getMessage());
            e.printStackTrace();
        }

        return results;
    }
}
