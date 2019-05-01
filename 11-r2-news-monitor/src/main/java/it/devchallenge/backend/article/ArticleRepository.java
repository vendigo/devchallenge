package it.devchallenge.backend.article;

import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

/**
 * Repository for working with articles.
 * Implementation is generated in runtime by Spring Data JPA.
 * Rest services is generated in runtime based on this interface by Spring Data Rest
 */
@SuppressWarnings("SpringCacheAnnotationsOnInterfaceInspection")
@CacheConfig(cacheNames = "articles")
public interface ArticleRepository extends Repository<Article, Long> {
    //Methods for inner usage. Not exposed to REST API
    @RestResource(exported = false)
    void save(Article entity);

    @RestResource(exported = false)
    void deleteAll();

    @RestResource(exported = false)
    List<Article> findAllByIdGreaterThanEqual(Long articleId);

    @RestResource(exported = false)
    List<Article> findAll();

    //Methods exposed to REST API
    @Cacheable
    Page<Article> findAll(Pageable pageable);

    @Cacheable
    Optional<Article> findByUrl(@Param("url") String url);

    Article findOne(Long id);

    @Query("SELECT a FROM Article a WHERE (SELECT count(s) FROM Snapshot s WHERE s.articleId = a.id) > 1")
    Page<Article> findChanged(Pageable pageable);

    @Query("SELECT a FROM Article a JOIN Snapshot s ON a.id = s.articleId WHERE s.content is null")
    Page<Article> findDeleted(Pageable pageable);
}
