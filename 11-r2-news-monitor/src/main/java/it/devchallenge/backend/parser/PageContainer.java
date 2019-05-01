package it.devchallenge.backend.parser;

import java.util.List;

import lombok.Value;

/**
 * Represents one parsed page.
 * It holds content of all articles as well as link to the next page.
 */
@Value
class PageContainer {
    private final List<ArticleContainer> articles;
    private final String nextPageUrl;
}
