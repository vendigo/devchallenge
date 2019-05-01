package it.devchallenge.cssgrabber.web;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.AllOf.allOf;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import it.devchallenge.cssgrabber.cache.CacheService;
import it.devchallenge.cssgrabber.css.CssGrabberService;

@RunWith(MockitoJUnitRunner.class)
public class CssGrabberControllerTest {

    private static final String GOOGLE_URL = "google.com";
    private static final String GOOGLE_CSS = "body{}";
    private static final String YOUTUBE_URL = "youtube.com";
    private static final String YOUTUBE_CSS = "h1{}";
    @Mock
    private CssGrabberService cssGrabberService;
    @Mock
    private CacheService cacheService;
    private CssGrabberController controller;

    @Before
    public void setUp() {
        controller = new CssGrabberController(cssGrabberService, cacheService);
    }

    @Test
    public void invalidateWholeCache() {
        controller.invalidateWholeCache();
        Mockito.verify(cacheService).invalidateCache();
    }

    @Test
    public void invalidateCacheForUrl() {
        controller.invalidateForUrl(GOOGLE_URL);
        Mockito.verify(cacheService).invalidateCache(GOOGLE_URL);
    }

    @Test
    public void styles() {
        when(cssGrabberService.getCss(YOUTUBE_URL)).thenReturn(YOUTUBE_CSS);
        when(cssGrabberService.getCss(GOOGLE_URL)).thenReturn(GOOGLE_CSS);
        Map<String, String> result = controller.styles(Arrays.asList(GOOGLE_URL, YOUTUBE_URL)).block();
        assertThat(result, allOf(
            hasEntry(YOUTUBE_URL, YOUTUBE_CSS),
            hasEntry(GOOGLE_URL, GOOGLE_CSS)
        ));
        assertThat(result.keySet(), hasSize(2));
    }
}
