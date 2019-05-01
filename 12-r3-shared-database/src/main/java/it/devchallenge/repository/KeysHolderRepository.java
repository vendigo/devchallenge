package it.devchallenge.repository;


import org.springframework.data.couchbase.repository.ReactiveCouchbaseRepository;

import it.devchallenge.model.KeysHolder;

public interface KeysHolderRepository extends ReactiveCouchbaseRepository<KeysHolder, String> {
}
