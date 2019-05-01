package it.devchallenge.backend.article;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ArticleRepositoryIntTest {
    @Autowired
    private ArticleRepository articleRepository;

    @Before
    public void setUp() throws Exception {
        articleRepository.deleteAll();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void findAllByIdGreaterThanEqual() throws Exception {
        Article article1 = new Article("article1");
        Article article2 = new Article("article2");
        Article article3 = new Article("article3");

        articleRepository.save(article1);
        articleRepository.save(article2);
        articleRepository.save(article3);

        List<Article> actualResult = articleRepository.findAllByIdGreaterThanEqual(article2.getId());
        assertThat(actualResult, hasSize(2));
        assertThat(actualResult, hasItems(
                hasProperty("url", equalTo("article2")),
                hasProperty("url", equalTo("article3"))
        ));
    }
}
