package it.devchallenge.backend.parser;

import static it.devchallenge.backend.snapshot.Snapshot.CONTENT_MAX_LENGTH;
import static org.apache.commons.lang3.StringUtils.abbreviate;

import lombok.Value;

/**
 * Container for holding result of parsing article
 */
@Value
public class ArticleContainer {
    private final String url;
    private final String content;
    private final int pageNumber;
    private final int onPageNumber;

    public ArticleContainer(String url, String content, int pageNumber, int onPageNumber) {
        this.url = url;
        this.content = abbreviate(content, CONTENT_MAX_LENGTH);
        this.pageNumber = pageNumber;
        this.onPageNumber = onPageNumber;
    }
}
