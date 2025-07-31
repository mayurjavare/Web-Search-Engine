package com.example.searchapi.model;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Document(collection = "cached_image_query")
public class CachedImageQuery 
{
	@Id
	private String id;
	private String query;
	private List<ImageSearchResult> results;
	private Date createdAt;
}
