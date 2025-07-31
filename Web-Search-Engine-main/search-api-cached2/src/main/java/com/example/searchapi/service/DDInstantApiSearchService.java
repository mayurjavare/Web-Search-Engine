package com.example.searchapi.service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service("ddInstantSearchService")
public class DDInstantApiSearchService{
	
	public String getInstantAnswer(String query) throws IOException, InterruptedException {
        String url = "https://api.duckduckgo.com/?q=" + URLEncoder.encode(query, StandardCharsets.UTF_8) + "&format=json";
        System.out.println(url);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET().headers("Accept","application/json").header("User-Agent", "Mozilla/5.0")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response);
        
        JSONObject json = new JSONObject(response.body());
        if(json.has("RelatedTopics"))
        {
        	JSONArray related = json.getJSONArray("RelatedTopics");
        	JSONArray limited = new JSONArray();
        	for(int i =0 ; i< Math.min(2,related.length());i++)
        	{
        		limited.put(related.get(i));
        	}
        	json.put("RelatedTopic", limited);
        }
        return json.toString();
    }
}
