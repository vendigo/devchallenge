package it.devchallenge.backend.article;

import java.util.List;
import java.util.Map;
import java.util.Set;

import it.devchallenge.backend.parser.ArticleContainer;

/**
 * Provides operations for working with articles.
 */
public interface ArticleService {
    /**
     * Returns latest versions of all articles newer than given
     * @param articleUrl - url of given article
     * @return Map url to content
     */
    Map<String, String> findArticlesNewerThan(String articleUrl);

    /**
     * Returns mapping articleUrl to articleId
     */
    Map<String, Long> getUrlToArticleId();

    /**
     * Saves new articles and first snapshots for them
     */
    List<Article> saveNewArticles(List<ArticleContainer> articles);

    /**
     * Mark article as deleted - create new snapshot with null content
     * @param articleIds article ids to delete
     */
    void markArticlesAsDeleted(Set<Long> articleIds);

    /**
     * Save new snapshots
     * @param articleIdToContent Map article id to content
     */
    void saveUpdates(Map<Long, String> articleIdToContent);
}
