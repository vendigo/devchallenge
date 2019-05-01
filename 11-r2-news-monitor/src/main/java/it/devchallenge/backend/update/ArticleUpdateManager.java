package it.devchallenge.backend.update;

import static it.devchallenge.backend.utils.CommonUtils.pickFirstStrategy;
import static java.util.stream.Collectors.*;
import static org.apache.commons.lang3.Validate.notNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;

import it.devchallenge.backend.article.ArticleService;
import it.devchallenge.backend.parser.ArticleContainer;
import it.devchallenge.backend.parser.PageParser;
import lombok.extern.slf4j.Slf4j;

/**
 * Parses site and save changes to database
 */
@Component
@Slf4j
public class ArticleUpdateManager {
    private static final int FIRST_ELEMENT_INDEX = 0;
    private final PageParser parser;
    private final ArticleService articleService;

    @Autowired
    public ArticleUpdateManager(PageParser parser, ArticleService articleService) {
        this.parser = notNull(parser);
        this.articleService = notNull(articleService);
    }

    @CacheEvict(cacheNames = {"articles", "snapshots"})
    public void updateArticles(int pagesToUpdate) {
        log.info("Updating {} pages", pagesToUpdate);
        List<ArticleContainer> articles = parser.parseContent(pagesToUpdate);
        if (!articles.isEmpty()) {
            String oldestArticleUrl = articles.get(FIRST_ELEMENT_INDEX).getUrl();
            Map<String, String> prevState = articleService.findArticlesNewerThan(oldestArticleUrl);
            Map<String, String> currentState = articles.stream()
                    .collect(toMap(ArticleContainer::getUrl, ArticleContainer::getContent, pickFirstStrategy()));

            MapDifference<String, String> difference = Maps.difference(prevState, currentState);
            if (!difference.areEqual()) {
                Map<String, Long> urlToArticleId = articleService.getUrlToArticleId();
                processDeleted(urlToArticleId, difference.entriesOnlyOnLeft());
                processChanged(urlToArticleId, difference.entriesDiffering());
                processNew(articles, difference.entriesOnlyOnRight());
            } else {
                log.info("No changes detected");
            }
        } else {
            log.warn("No articles were parsed.");
        }
    }

    private void processDeleted(Map<String, Long> urlToArticleId, Map<String, String> urlToContent) {
        if (!urlToContent.isEmpty()) {
            log.info("Found {} deleted articles", urlToContent.size());
            Set<Long> deletedArticleIds = urlToContent.keySet().stream().map(urlToArticleId::get).collect(toSet());
            articleService.markArticlesAsDeleted(deletedArticleIds);
        }
    }

    private void processChanged(Map<String, Long> urlToArticleId,
                                Map<String, MapDifference.ValueDifference<String>> differenceMap) {
        if (!differenceMap.isEmpty()) {
            log.info("Found {} changed articles", differenceMap.size());
            Map<Long, String> idToContent = new HashMap<>();
            differenceMap.forEach((url, diff)-> idToContent.put(urlToArticleId.get(url), diff.rightValue()));
            articleService.saveUpdates(idToContent);
        }
    }

    private void processNew(List<ArticleContainer> articles, Map<String, String> newUrlToContent) {
        if (!newUrlToContent.isEmpty()) {
            log.info("Found {} new articles", newUrlToContent.size());
            List<ArticleContainer> newArticles = articles.stream()
                    .filter(a -> newUrlToContent.containsKey(a.getUrl()))
                    .collect(toList());
            articleService.saveNewArticles(newArticles);
        }
    }
}
