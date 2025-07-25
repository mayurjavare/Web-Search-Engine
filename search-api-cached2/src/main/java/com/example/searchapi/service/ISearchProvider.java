package com.example.searchapi.service;

import java.util.List;

import com.example.searchapi.model.SearchResult;

public interface ISearchProvider {
	
	List<SearchResult> search(String query) throws Exception;

}
