package it.devchallenge.repository;


import org.springframework.data.couchbase.repository.ReactiveCouchbaseRepository;

import it.devchallenge.model.DataItem;

public interface DataItemRepository extends ReactiveCouchbaseRepository<DataItem, String> {
}
