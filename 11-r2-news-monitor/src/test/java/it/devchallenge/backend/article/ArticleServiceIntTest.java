package it.devchallenge.backend.article;

import static it.devchallenge.backend.TestUtils.entry;
import static it.devchallenge.backend.TestUtils.mapOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsMapContaining.hasEntry;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import it.devchallenge.backend.parser.ArticleContainer;
import it.devchallenge.backend.snapshot.Snapshot;
import it.devchallenge.backend.snapshot.SnapshotRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ArticleServiceIntTest {
    private static final ArticleContainer ARTICLE_1 = new ArticleContainer("article1", "Article 1", 1, 1);
    private static final ArticleContainer ARTICLE_2 = new ArticleContainer("article2", "Article 2", 1, 0);
    private static final ArticleContainer ARTICLE_3 = new ArticleContainer("article3", "Article 3", 0, 0);
    private static final List<ArticleContainer> ARTICLES = Arrays.asList(ARTICLE_1, ARTICLE_2, ARTICLE_3);

    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private SnapshotRepository snapshotRepository;
    @Autowired
    private ArticleService articleService;

    @Before
    public void setUp() throws Exception {
        snapshotRepository.deleteAll();
        articleRepository.deleteAll();
    }

    @Test
    public void getUrlToArticleId() throws Exception {
        articleRepository.save(new Article("article1"));
        articleRepository.save(new Article("article2"));
        articleRepository.save(new Article("article3"));

        Map<String, Long> actualResult = articleService.getUrlToArticleId();
        assertThat(actualResult.values(), hasSize(3));
        assertThat(actualResult, hasEntry(equalTo("article1"), notNullValue()));
        assertThat(actualResult, hasEntry(equalTo("article2"), notNullValue()));
        assertThat(actualResult, hasEntry(equalTo("article3"), notNullValue()));
    }

    @Test
    public void saveNewArticles() {
        List<Article> articles = articleService.saveNewArticles(ARTICLES);
        assertThat(articles, hasSize(3));
        assertThat(articleRepository.findAll(), hasSize(3));
        assertThat(snapshotRepository.findAll(), hasSize(3));
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void findArticlesNewerThan() throws Exception {
        List<Article> articles = articleService.saveNewArticles(ARTICLES);
        Article article2 = articles.stream()
                .filter(article -> Objects.equals(article.getUrl(), "article2"))
                .findAny()
                .get();
        snapshotRepository.save(new Snapshot(article2.getId(), "Article 2. Version 2"));

        Map<String, String> actualResult = articleService.findArticlesNewerThan(article2.getUrl());
        Map<String, String> expectedResult = mapOf(
                entry("article2", "Article 2. Version 2"),
                entry("article3", "Article 3"));
        assertThat(actualResult, equalTo(expectedResult));
    }
}
