package it.devchallenge.cssgrabber.css;

/**
 * Encapsulates all business logic of getting used styles for given url. Uses ChromeClient.
 */
public interface CssGrabberService {

    String getCss(String url);
}
