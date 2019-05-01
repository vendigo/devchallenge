package it.devchallenge.cssgrabber.css;

import static it.devchallenge.cssgrabber.cache.CacheService.STYLES_CACHE_NAME;
import static java.util.stream.Collectors.toMap;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import it.devchallenge.cssgrabber.cache.CacheService;
import it.devchallenge.cssgrabber.chrome.ChromeClient;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
@Service
public class CssGrabberServiceImpl implements CssGrabberService {

    private static final String SELECTOR = "selector";
    private static final String CSS_TEXT = "cssText";
    private static final Pattern CSS_BLOCK_PATTERN = Pattern.compile("(.*) \\{ (.*) }");
    private static final int INNER_VALUES_GROUP_INDEX = 2;
    private static final String EMPTY_STRING = "";
    private static final int TWO_ITEMS = 2;
    private static final int FIRST_ITEM_INDEX = 0;
    private static final int SECOND_ITEM_INDEX = 1;

    private final ChromeClient chromeClient;
    private final CacheService cacheService;

    @Override
    @Cacheable(cacheNames = STYLES_CACHE_NAME, sync = true)
    public String getCss(String url) {
        try {
            log.info("Launching Chrome for url: {}", url);
            Map<String, Map<String, String>> styles = chromeClient.getUsedStyles(url)
                .stream()
                .map(this::createCssItem)
                .collect(LinkedHashMap::new, CssCollector::accumulate, CssCollector::combine);
            cacheService.markCacheTime(url);
            log.info("Finishing Grabbing styles for url: {}", url);
            return cssToString(styles);
        } catch (Exception e) {
            log.error("Error during processing url: {}", url);
            e.printStackTrace();
            return "ERROR: Failed to process url";
        }
    }

    private CssItem createCssItem(Map<String, String> map) {
        String selector = map.get(SELECTOR);
        Map<String, String> parsedCssValues = parseCssText(map.get(CSS_TEXT));
        return new CssItem(selector, parsedCssValues);
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    private Map<String, String> parseCssText(String cssText) {
        Matcher matcher = CSS_BLOCK_PATTERN.matcher(cssText);

        if (matcher.find()) {
            String innerValues = matcher.group(INNER_VALUES_GROUP_INDEX);
            return Stream.of(innerValues.split(";"))
                .map(property -> property.split(":"))
                .filter(pair -> pair.length == TWO_ITEMS)
                .collect(toMap(pair -> StringUtils.trimWhitespace(pair[FIRST_ITEM_INDEX]),
                    pair -> StringUtils.trimWhitespace(pair[SECOND_ITEM_INDEX]),
                    (l, r) -> r, LinkedHashMap::new));
        } else {
            return new HashMap<>();
        }
    }

    private String cssToString(Map<String, Map<String, String>> styles) {
        if (styles.isEmpty()) {
            return EMPTY_STRING;
        }

        StringBuilder stringBuilder = new StringBuilder();
        styles.forEach((selector, properties) -> {
            stringBuilder
                .append(selector)
                .append("{");
            properties.forEach((key, value) -> stringBuilder
                .append(key)
                .append(":")
                .append(value)
                .append(";"));
            stringBuilder
                .append("}");
        });
        return stringBuilder.toString();
    }
}
