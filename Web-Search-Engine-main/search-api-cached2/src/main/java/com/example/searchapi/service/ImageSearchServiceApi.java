package com.example.searchapi.service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.searchapi.model.CachedImageQuery;
import com.example.searchapi.model.ImageSearchResult;
import com.example.searchapi.repository.CachedImageQueryRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ImageSearchServiceApi 
{
	@Autowired
	private  CachedImageQueryRepository cachedImageQueryRepository ;
	
	@Value("${img.api.key}")
	private String apiKey ;
	
	private static final String imgSearchUrl= "https://serpapi.com/search.json?engine=google_images_light";
	
	public List<ImageSearchResult> searchImages(String query) {
        var cached = cachedImageQueryRepository.findByQuery(query);
        if (cached.isPresent()) {
            System.out.println("IMAGE CACHE HIT for: " + query);
            return cached.get().getResults();
        }

        System.out.println("IMAGE CACHE MISS — Calling SerpAPI for images: " + query);
        List<ImageSearchResult> results = new ArrayList<>();

        try {
            String encoded = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String url = imgSearchUrl + "&q=" + encoded + "&api_key=" + apiKey;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode json = new ObjectMapper().readTree(response.body());

            JsonNode images = json.get("images_results");
            if (images != null) {
                for (JsonNode item : images) {
                    ImageSearchResult result = new ImageSearchResult();
                    result.setTitle(item.has("title") ? item.get("title").asText() : "");
                    result.setImgUrl(item.has("thumbnail") ? item.get("thumbnail").asText() : null);
                    result.setSource(item.has("source") ? item.get("source").asText() : null);
                    results.add(result);
                }
            }

            CachedImageQuery cache = new CachedImageQuery();
            cache.setQuery(query);
            cache.setResults(results);
            cache.setCreatedAt(new Date()); // ✅ Correct usage
            cachedImageQueryRepository.save(cache);

            System.out.println("✅ Cached image results for: " + query);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return results;
    }
}
