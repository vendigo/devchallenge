package it.devchallenge.backend.article;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.Validate.notNull;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.devchallenge.backend.parser.ArticleContainer;
import it.devchallenge.backend.snapshot.Snapshot;
import it.devchallenge.backend.snapshot.SnapshotRepository;

@Service
public class ArticleServiceImpl implements ArticleService {
    private final ArticleRepository articleRepository;
    private final SnapshotRepository snapshotRepository;

    @Autowired
    public ArticleServiceImpl(ArticleRepository articleRepository, SnapshotRepository snapshotRepository) {
        this.articleRepository = notNull(articleRepository);
        this.snapshotRepository = notNull(snapshotRepository);
    }

    @Override
    public Map<String, Long> getUrlToArticleId() {
        return articleRepository.findAll().stream()
                .collect(toMap(Article::getUrl, Article::getId));
    }

    @Override
    public List<Article> saveNewArticles(List<ArticleContainer> articles) {
        return articles.stream().map(this::saveOne).collect(toList());
    }

    @Override
    public void markArticlesAsDeleted(Set<Long> articleIds) {
        articleIds.stream()
                .map(id -> new Snapshot(id, null))
                .forEach(snapshotRepository::save);
    }

    @Override
    public void saveUpdates(Map<Long, String> articleIdToContent) {
        articleIdToContent.forEach((articleId, content) -> snapshotRepository.save(new Snapshot(articleId, content)));
    }

    @Override
    public Map<String, String> findArticlesNewerThan(String articleUrl) {
        Optional<Article> byUrl = articleRepository.findByUrl(articleUrl);
        if (byUrl.isPresent()) {
            Long articleId = byUrl.get().getId();

            List<Article> articles = articleRepository.findAllByIdGreaterThanEqual(articleId);
            Map<Long, String> articleIdToUrl = articles.stream()
                    .collect(toMap(Article::getId, Article::getUrl));

            List<Snapshot> snapshots = snapshotRepository.findLatestForArticlesNewerThanGiven(articleId);
            return snapshots.stream()
                    .collect(toMap(s -> articleIdToUrl.get(s.getArticleId()), Snapshot::getContent));
        } else {
            return Collections.emptyMap();
        }
    }

    @Transactional
    private Article saveOne(ArticleContainer articleContainer) {
        Article article = new Article(articleContainer.getUrl());
        articleRepository.save(article);
        Snapshot snapshot = new Snapshot(article.getId(), articleContainer.getContent());
        snapshotRepository.save(snapshot);
        return article;
    }
}
