package com.example.searchapi.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.searchapi.model.SearchResult;
import com.example.searchapi.service.SearchService;

@RestController
@RequestMapping("/api")
public class SearchController {

	private final SearchService service;

	public SearchController(SearchService service) {
		this.service = service;
	}

	@CrossOrigin(origins = "http://localhost:5173")
	@GetMapping("/search")
	public List<SearchResult> search(@RequestParam String query) {
		return service.search(query);
	}

}
