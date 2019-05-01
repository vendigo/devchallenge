package it.devchallenge.cssgrabber.css;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.google.common.collect.ImmutableMap;

import it.devchallenge.cssgrabber.cache.CacheService;
import it.devchallenge.cssgrabber.chrome.ChromeClient;

@RunWith(MockitoJUnitRunner.class)
public class CssGrabberServiceImplTest {

    private static final Map<String, String> CSS_ITEM1 = ImmutableMap.<String, String>builder()
        .put("selector", "body")
        .put("cssText", "body { font-family: Arial; background-color: rgb(255, 255, 255); }")
        .build();
    private static final Map<String, String> CSS_ITEM2 = ImmutableMap.<String, String>builder()
        .put("selector", "p")
        .put("cssText", "p { font-size:12pt; font-family:Arial; margin-top:1pt; }")
        .build();
    private static final Map<String, String> CSS_ITEM3 = ImmutableMap.<String, String>builder()
        .put("selector", "p")
        .put("cssText", "p { font-weight:bold; font-size:18pt; }")
        .build();
    private static final String GOOGLE_URL = "google.com";
    private static final String GOOGLE_CSS = "body{font-family:Arial;background-color:rgb(255, 255, 255);}" +
        "p{font-size:18pt;font-family:Arial;margin-top:1pt;font-weight:bold;}";

    @Mock
    private ChromeClient chromeClient;
    @Mock
    private CacheService cacheService;
    private CssGrabberServiceImpl cssGrabberService;

    @Before
    public void setUp() {
        cssGrabberService = new CssGrabberServiceImpl(chromeClient, cacheService);
        doNothing().when(cacheService).markCacheTime(anyString());
    }

    @Test
    public void getCssMergeSameSelectors() {
        when(chromeClient.getUsedStyles(GOOGLE_URL)).thenReturn(Arrays.asList(CSS_ITEM1, CSS_ITEM2, CSS_ITEM3));
        String result = cssGrabberService.getCss(GOOGLE_URL);
        assertThat(result).isEqualTo(GOOGLE_CSS);
    }
}
