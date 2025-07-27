package com.example.searchapi.controller;

import java.io.IOException;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.searchapi.model.SearchResult;
import com.example.searchapi.service.AutoCompleteService;
import com.example.searchapi.service.DDInstantApiSearchService;
import com.example.searchapi.service.SearchService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class SearchController {

	private final SearchService service;

	@Autowired
	private AutoCompleteService autoCompleteService;

	@Autowired
	private DDInstantApiSearchService ddInstantApiSearchService;

	public SearchController(SearchService service) {
		this.service = service;
	}

	@GetMapping("/search")
	public List<SearchResult> search(@RequestParam String query, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int pageSize) {
		return service.search(query, page, pageSize);
	}

	@GetMapping("/autoComplete")
	public ResponseEntity<String> autoComplete(@RequestParam String query) throws IOException, InterruptedException {
		String suggestion = autoCompleteService.getSuggestion(query);
		return ResponseEntity.ok(suggestion);
	}

	@GetMapping("/search/ddg-instant")
	public ResponseEntity<String> getInstantAnswer(@RequestParam String query)
	        throws IOException, InterruptedException {

	    String rawResponse = ddInstantApiSearchService.getInstantAnswer(query);
	    JSONObject ddgJson = new JSONObject(rawResponse);

	    JSONObject filtered = new JSONObject();
	    filtered.put("AbstractText", ddgJson.optString("AbstractText", ""));
	    filtered.put("Answer", ddgJson.optString("Answer", ""));
	    filtered.put("Heading", ddgJson.optString("Heading", ""));

	    String abstractText = ddgJson.optString("AbstractText", "");
	    boolean hasAbstract = abstractText != null && !abstractText.isBlank();

	    // Fallback: check RelatedTopics if no AbstractText
	    if (!hasAbstract) {
	        JSONArray relatedTopics = ddgJson.optJSONArray("RelatedTopics");
	        JSONArray simplifiedTopics = new JSONArray();

	        if (relatedTopics != null) {
	            for (int i = 0; i < relatedTopics.length(); i++) {
	                JSONObject item = relatedTopics.getJSONObject(i);

	                if (item.has("Topics")) {
	                    JSONArray nested = item.getJSONArray("Topics");
	                    for (int j = 0; j < nested.length(); j++) {
	                        JSONObject subItem = nested.getJSONObject(j);
	                        JSONObject simple = new JSONObject();
	                        simple.put("Text", subItem.optString("Text", ""));
	                        simple.put("FirstURL", subItem.optString("FirstURL", ""));
	                        simplifiedTopics.put(simple);
	                    }
	                } else {
	                    JSONObject simple = new JSONObject();
	                    simple.put("Text", item.optString("Text", ""));
	                    simple.put("FirstURL", item.optString("FirstURL", ""));
	                    simplifiedTopics.put(simple);
	                }
	            }
	        }

	        filtered.put("RelatedTopics", simplifiedTopics);
	        filtered.put("RelatedTopicsCount", simplifiedTopics.length());
	    }

	    boolean hasInstantAnswer =
	        !filtered.optString("AbstractText", "").isBlank() ||
	        !filtered.optString("Answer", "").isBlank() ||
	        (filtered.has("RelatedTopics") && filtered.getJSONArray("RelatedTopics").length() > 0);

	    if (!hasInstantAnswer) {
	        return ResponseEntity.noContent().build(); // 204 No Content if nothing to show
	    }

	    return ResponseEntity.ok(filtered.toString());
	}

}
