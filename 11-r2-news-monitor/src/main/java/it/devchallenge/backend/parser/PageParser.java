package it.devchallenge.backend.parser;

import java.util.List;

/**
 * Parses target site and returns content in {@link ArticleContainer}
 */
public interface PageParser {
    List<ArticleContainer> parseContent(int pagesToParse);
}
