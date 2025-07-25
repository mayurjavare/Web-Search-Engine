package com.example.searchapi.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.searchapi.model.CachedQuery;
import com.example.searchapi.model.SearchResult;
import com.example.searchapi.repository.CachedQueryRepository;

@Service
public class SearchService {
	
	private final CachedQueryRepository repo;
    private final List<ISearchProvider> searchProviders;

    @Autowired
    public SearchService(CachedQueryRepository repo, List<ISearchProvider> searchProviders) {
        this.repo = repo;
        this.searchProviders = searchProviders;
    }

    public List<SearchResult> search(String query) {
        var cached = repo.findByQuery(query);
        if (cached.isPresent()) {
            System.out.println("CACHE HIT for query: " + query);
            return cached.get().getResults();
        }

        System.out.println("CACHE MISS — Running all search providers in parallel");

        List<CompletableFuture<List<? extends Object>>> futures = searchProviders.stream()
                .map(provider -> CompletableFuture.supplyAsync(() -> {
                    try {
                        return provider.search(query);
                    } catch (Exception e) {
                        System.out.println("❌ Error in " + provider.getClass().getSimpleName() + ": " + e.getMessage());
                        return Collections.emptyList();
                    }
                }))
                .toList();

        List<SearchResult> allResults = (List<SearchResult>) futures.stream()
                .map(CompletableFuture::join)
                .flatMap(List::stream)
                .collect(Collectors.toList());

        // Deduplicate by URL
        Map<String, SearchResult> deduped = new LinkedHashMap<>();
        for (SearchResult result : allResults) {
            deduped.putIfAbsent(result.getUrl(), result);
        }

        List<SearchResult> finalResults = new ArrayList<>(deduped.values());

        // Cache result
        CachedQuery cq = new CachedQuery();
        cq.setQuery(query);
        cq.setResults(finalResults);
        cq.setCreatedAt(new java.util.Date());
        repo.save(cq);

        System.out.println("✅ Cached combined async results for query: " + query);
        return finalResults;
    }

}
