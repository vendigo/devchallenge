package it.devchallenge.backend.snapshot;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import it.devchallenge.backend.article.Article;
import it.devchallenge.backend.article.ArticleRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SnapshotRepositoryIntTest {
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private SnapshotRepository snapshotRepository;

    @Before
    public void setUp() throws Exception {
        snapshotRepository.deleteAll();
        articleRepository.deleteAll();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void findLatest() throws Exception {
        Article article1 = new Article("article1");
        articleRepository.save(article1);
        Article article2 = new Article("article2");
        articleRepository.save(article2);

        snapshotRepository.save(new Snapshot(article1.getId(), "Article 1. Version 1"));
        snapshotRepository.save(new Snapshot(article1.getId(), "Article 1. Version 2"));
        snapshotRepository.save(new Snapshot(article2.getId(), "Article 2."));

        Page<Snapshot> page = snapshotRepository.findLatest(new PageRequest(0, 20));
        List<Snapshot> actualResult = page.getContent();
        assertThat(actualResult, hasSize(2));
        assertThat(actualResult, hasItems(
                hasProperty("content", equalTo("Article 1. Version 2")),
                hasProperty("content", equalTo("Article 2."))
        ));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void findLatestForArticlesNewerThanGiven() throws Exception {
        Article article1 = new Article("article1");
        articleRepository.save(article1);
        Article article2 = new Article("article2");
        articleRepository.save(article2);
        Article article3 = new Article("article3");
        articleRepository.save(article3);

        snapshotRepository.save(new Snapshot(article1.getId(), "Article 1. Version 1"));
        snapshotRepository.save(new Snapshot(article1.getId(), "Article 1. Version 2"));
        snapshotRepository.save(new Snapshot(article2.getId(), "Article 2. Version 1"));
        snapshotRepository.save(new Snapshot(article3.getId(), "Article 3."));
        snapshotRepository.save(new Snapshot(article2.getId(), "Article 2. Version 2"));

        List<Snapshot> actualResult = snapshotRepository.findLatestForArticlesNewerThanGiven(article2.getId());
        assertThat(actualResult, hasSize(2));
        assertThat(actualResult, hasItems(
                hasProperty("content", equalTo("Article 2. Version 2")),
                hasProperty("content", equalTo("Article 3."))
        ));
    }
}
