package it.devchallenge.backend.grabber;

import org.jsoup.select.Elements;

/**
 * Encapsulates logic with accessing remote server and getting html content
 */
public interface PageContentProvider {
    /**
     * Returns html content of the given url
     */
    Elements getPageContent(String url);
}
