package it.devchallenge.backend.snapshot;

import java.util.List;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.rest.core.annotation.RestResource;

/**
 * Repository for working with snapshots.
 * Implementation is generated in runtime by Spring Data JPA.
 * Rest services is generated in runtime based on this interface by Spring Data Rest
 */
@SuppressWarnings("SpringCacheAnnotationsOnInterfaceInspection")
@CacheConfig(cacheNames = "snapshots")
public interface SnapshotRepository extends Repository<Snapshot, Long> {
    //Methods for inner usage. Not exposed to REST API
    @RestResource(exported = false)
    void save(Snapshot entity);

    @RestResource(exported = false)
    void deleteAll();

    @RestResource(exported = false)
    @Query("SELECT s FROM Snapshot s WHERE s.articleId >= ?1 AND s.id = (" +
            "SELECT MAX(ss.id) FROM Snapshot  ss WHERE s.articleId = ss.articleId)")
    List<Snapshot> findLatestForArticlesNewerThanGiven(Long articleId);

    @RestResource(exported = false)
    List<Snapshot> findAll();

    //Methods exposed to REST API
    @Cacheable
    Snapshot findOne(Long id);

    @Cacheable
    Page<Snapshot> findAll(Pageable pageable);

    @Cacheable
    @Query("SELECT s FROM Snapshot s WHERE s.id = (" +
            "SELECT MAX(ss.id) FROM Snapshot  ss WHERE s.articleId = ss.articleId)")
    Page<Snapshot> findLatest(Pageable pageable);

}
