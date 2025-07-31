package com.example.searchapi.service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Service;

@Service
public class AutoCompleteService {
	public String getSuggestion(String query) throws IOException, InterruptedException {
		String url = "https://duckduckgo.com/ac/?q=" + URLEncoder.encode(query, StandardCharsets.UTF_8);
		HttpClient client = HttpClient.newBuilder()
	            .followRedirects(HttpClient.Redirect.NORMAL)
	            .build();
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).header("Accept", "application/json")
				.header("User-Agent", "Mozilla/5.0").build();
		
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		return response.body();
	}
}
