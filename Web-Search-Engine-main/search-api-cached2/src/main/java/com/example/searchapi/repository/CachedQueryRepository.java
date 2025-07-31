package com.example.searchapi.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.searchapi.model.CachedQuery;

public interface CachedQueryRepository extends MongoRepository<CachedQuery, String> 
{
	Optional<CachedQuery> findByQuery(String query);
}
	