package com.example.searchapi.service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.searchapi.model.CachedQuery;
import com.example.searchapi.model.SearchResult;
import com.example.searchapi.repository.CachedQueryRepository;



@Service
public class SearchService {

	private final CachedQueryRepository repo;
	private final List<ISearchProvider> searchProviders;

	private final Set<String> stopwords = Set.of("what", "is", "the", "a", "an", "of", "to", "and", "in", "on");

	private List<String> extractKeywords(String query) {
		return List.of(query.toLowerCase().split("\\W+")).stream().filter(word -> !stopwords.contains(word))
				.collect(Collectors.toList());
	}

	public SearchService(CachedQueryRepository repo, List<ISearchProvider> searchProviders) {
		super();
		this.repo = repo;
		this.searchProviders = searchProviders;
	}

//    @Autowired
//    public SearchService(CachedQueryRepository repo, List<ISearchProvider> searchProviders) {
//        this.repo = repo;
//        this.searchProviders = searchProviders;
//    }

	public List<SearchResult> search(String query, int page, int size) {
		List<SearchResult> finalResults;

		var cached = repo.findByQuery(query);
		if (cached.isPresent()) {
			System.out.println("CACHE HIT for query: " + query);
			finalResults = cached.get().getResults();
		} else {
			System.out.println("CACHE MISS — Running all search providers in parallel");

			List<CompletableFuture<List<? extends Object>>> futures = searchProviders.stream()
					.map(provider -> CompletableFuture.supplyAsync(() -> {
						try {
							return provider.search(query);
						} catch (Exception e) {
							System.out.println(
									"❌ Error in " + provider.getClass().getSimpleName() + ": " + e.getMessage());
							return Collections.emptyList();
						}
					})).toList();

			List<SearchResult> allResults = (List<SearchResult>) futures.stream().map(CompletableFuture::join)
					.flatMap(List::stream).collect(Collectors.toList());

			// Deduplicate by URL
			Map<String, SearchResult> deduped = new LinkedHashMap<>();
			for (SearchResult result : allResults) {
				deduped.putIfAbsent(result.getUrl(), result);
			}

			finalResults = new ArrayList<>(deduped.values());

			// Ranking logic
			List<String> keywords = extractKeywords(query);
			int rank = 0;
			for (SearchResult r : finalResults) {
				double score = 0;
				for (String keyword : keywords) {
					if (r.getTitle() != null) {
						String title = r.getTitle().toLowerCase();
						for (String word : title.split("\\W+")) {
							if (word.equals(keyword))
								score += 4;
						}
						if (title.contains(keyword))
							score += 1;
					}

					if (r.getDescription() != null) {
						String desc = r.getDescription().toLowerCase();
						for (String word : desc.split("\\W+")) {
							if (word.equals(keyword))
								score += 2;
						}
						if (desc.contains(keyword))
							score += 0.5;
					}

					if (r.getUrl() != null && r.getUrl().toLowerCase().contains("cricketwireless")) {
						score -= 10;
					}
				}

				if (r.getUrl() != null && (r.getUrl().contains(".gov") || r.getUrl().contains(".edu")
						|| r.getUrl().contains(".org"))) {
					score += 5;
				}

				r.setScore(score);
				r.setQuery(query);
				r.setOriginalRank(rank);
				rank++;
			}

			finalResults.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));

			// Cache result
			CachedQuery cq = new CachedQuery();
			cq.setQuery(query);
			cq.setResults(finalResults);
			cq.setCreatedAt(new java.util.Date());
			repo.save(cq);

			System.out.println("✅ Cached combined async results for query: " + query);
		}

		// 🧮 Pagination logic at the final level
		int start = page * size;
		int end = Math.min(start + size, finalResults.size());

		if (start >= finalResults.size()) {
			return Collections.emptyList();
		}

		return finalResults.subList(start, end);
	}
}
