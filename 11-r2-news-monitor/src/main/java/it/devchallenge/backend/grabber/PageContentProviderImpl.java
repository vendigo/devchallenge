package it.devchallenge.backend.grabber;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.SneakyThrows;

/**
 * Implementation based on JSoup library
 */
@Component
public class PageContentProviderImpl implements PageContentProvider {
    private static final String CONTENT_SELECTOR = "div.bg1-content.col-md-8.col-sm-8";

    @Value("${grabber.home.url}")
    private String rootUrl;

    @SneakyThrows
    @Override
    public Elements getPageContent(String url) {
        return Jsoup.connect(rootUrl+url)
                .get()
                .select(CONTENT_SELECTOR);
    }
}
