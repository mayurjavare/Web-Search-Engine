package com.example.searchapi.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.searchapi.model.CachedImageQuery;

public interface CachedImageQueryRepository extends MongoRepository<CachedImageQuery, String> 
{
	Optional<CachedImageQuery>  findByQuery(String query);
}
