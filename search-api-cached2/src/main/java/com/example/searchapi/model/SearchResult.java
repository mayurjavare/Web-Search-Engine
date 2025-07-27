package com.example.searchapi.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.annotation.Generated;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Document(collection = "search_results")
public class SearchResult {

    @Id
    private String id;

    private String query;         // Search query for which this result was fetched
    private String title;
    private String url;
    private String description;
    private String source;        // Optional: Which search provider (Brave, SerpAPI, etc.)
    private int originalRank;     // Position in original API response
    private double score;         // Relevance score
}
