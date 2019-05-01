package it.devchallenge.backend.update;

import static it.devchallenge.backend.TestUtils.entry;
import static it.devchallenge.backend.TestUtils.mapOf;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Sets;

import it.devchallenge.backend.article.ArticleService;
import it.devchallenge.backend.parser.ArticleContainer;
import it.devchallenge.backend.parser.PageParser;

@RunWith(MockitoJUnitRunner.class)
public class ArticleUpdateManagerTest {
    private static final ArticleContainer ARTICLE_1 = new ArticleContainer("article1", "Article 1", 1, 1);
    private static final ArticleContainer ARTICLE_2 = new ArticleContainer("article2", "Article 2", 1, 0);
    private static final ArticleContainer ARTICLE_3 = new ArticleContainer("article3", "Article 3", 0, 1);
    private static final ArticleContainer ARTICLE_4 = new ArticleContainer("article4", "Article 4", 0, 0);
    @Mock
    private ArticleService articleService;
    @Mock
    private PageParser pageParser;
    private ArticleUpdateManager updateManager;

    @Before
    public void setUp() throws Exception {
        updateManager = new ArticleUpdateManager(pageParser, articleService);
    }

    @Test
    public void runOnEmptyDb() throws Exception {
        when(pageParser.parseContent(2)).thenReturn(Arrays.asList(
                ARTICLE_1, ARTICLE_2, ARTICLE_3, ARTICLE_4));
        when(articleService.findArticlesNewerThan("article1")).thenReturn(Collections.emptyMap());
        updateManager.updateArticles(2);

        verify(articleService).getUrlToArticleId();
        verify(articleService).findArticlesNewerThan("article1");

        List<ArticleContainer> newArticles = Arrays.asList(ARTICLE_1, ARTICLE_2, ARTICLE_3, ARTICLE_4);

        verify(articleService).saveNewArticles(newArticles);
        verifyNoMoreInteractions(articleService);
    }

    @Test
    public void runWithoutChanges() throws Exception {
        when(pageParser.parseContent(2)).thenReturn(Arrays.asList(
                ARTICLE_1, ARTICLE_2, ARTICLE_3, ARTICLE_4));

        Map<String, String> prevMap = mapOf(
                entry("article1", "Article 1"),
                entry("article2", "Article 2"),
                entry("article3", "Article 3"),
                entry("article4", "Article 4"));

        when(articleService.findArticlesNewerThan("article1")).thenReturn(prevMap);
        updateManager.updateArticles(2);

        verify(articleService).findArticlesNewerThan("article1");
        verifyNoMoreInteractions(articleService);
    }

    @Test
    public void markDeletedArticles() throws Exception {
        when(pageParser.parseContent(2)).thenReturn(Arrays.asList(
                ARTICLE_1, ARTICLE_3, ARTICLE_4));

        Map<String, String> prevMap = mapOf(
                entry("article1", "Article 1"),
                entry("article2", "Article 2"),
                entry("article3", "Article 3"),
                entry("article4", "Article 4"));

        when(articleService.findArticlesNewerThan("article1")).thenReturn(prevMap);

        Map<String, Long> urlToIdMap = mapOf(
                entry("article1", 1L),
                entry("article2", 2L),
                entry("article3", 3L),
                entry("article4", 4L));
        when(articleService.getUrlToArticleId()).thenReturn(urlToIdMap);

        updateManager.updateArticles(2);

        verify(articleService).findArticlesNewerThan("article1");
        verify(articleService).getUrlToArticleId();
        verify(articleService).markArticlesAsDeleted(Sets.newHashSet(2L));
        verifyNoMoreInteractions(articleService);
    }

    @Test
    public void updateChangedArticles() throws Exception {
        when(pageParser.parseContent(2)).thenReturn(Arrays.asList(
                ARTICLE_1, ARTICLE_2, ARTICLE_3, ARTICLE_4));

        Map<String, String> prevMap = mapOf(
                entry("article1", "Article 1"),
                entry("article2", "Old content"),
                entry("article3", "Old content"),
                entry("article4", "Article 4"));

        when(articleService.findArticlesNewerThan("article1")).thenReturn(prevMap);

        Map<String, Long> urlToIdMap = mapOf(
                entry("article1", 1L),
                entry("article2", 2L),
                entry("article3", 3L),
                entry("article4", 4L));
        when(articleService.getUrlToArticleId()).thenReturn(urlToIdMap);

        updateManager.updateArticles(2);

        verify(articleService).findArticlesNewerThan("article1");
        verify(articleService).getUrlToArticleId();

        Map<Long, String> updatesMap = mapOf(
                entry(2L, "Article 2"),
                entry(3L, "Article 3"));

        verify(articleService).saveUpdates(updatesMap);
        verifyNoMoreInteractions(articleService);
    }
}
