package it.devchallenge.backend.parser;

import static it.devchallenge.backend.utils.CommonUtils.pickFirstStrategy;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.Validate.notNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.devchallenge.backend.grabber.PageContentProvider;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation for parsing site http://brovary-rada.gov.ua/
 */
@Component
@Slf4j
public class BrovaryRadaParser implements PageParser {
    private static final String NEXT_BLOCK_LINK = "»»";
    private static final String ARTICLE_LINKS_SELECTOR = "div > a";
    private static final String PAGINATION_LINKS_SELECTOR = "ul.pagination.pagination-lg a";
    private static final String HREF_ATTR = "href";
    private static final String DOCUMENTS_URL = "/documents";

    private final PageContentProvider pageContentProvider;

    @Autowired
    public BrovaryRadaParser(PageContentProvider pageContentProvider) {
        this.pageContentProvider = notNull(pageContentProvider);
    }

    @Override
    public List<ArticleContainer> parseContent(int pagesToParse) {
        int pageIndex = 1;
        String pageUrl = DOCUMENTS_URL;
        List<ArticleContainer> articles = new ArrayList<>();

        while (pageIndex <= pagesToParse && pageUrl != null) {
            PageContainer pageContainer = parsePage(pageUrl, pageIndex);
            articles.addAll(pageContainer.getArticles());
            pageUrl = pageContainer.getNextPageUrl();
            pageIndex++;
        }

        articles.sort(comparing(ArticleContainer::getPageNumber)
                .thenComparing(ArticleContainer::getOnPageNumber).reversed());
        return articles;
    }

    @SneakyThrows
    private PageContainer parsePage(String pageUrl, int currentPageIndex) {
        Elements contentBlock = pageContentProvider.getPageContent(pageUrl);

        Elements articleLinks = contentBlock.select(ARTICLE_LINKS_SELECTOR);
        List<ArticleContainer> content = collectContent(articleLinks, currentPageIndex);

        Elements paginationLinks = contentBlock.select(PAGINATION_LINKS_SELECTOR);
        String nextPageUrl = findLinkToNextPage(paginationLinks, currentPageIndex);
        return new PageContainer(content, nextPageUrl);
    }

    private String findLinkToNextPage(Elements paginationLinks, int currentPageIndex) {
        String nextPageIndex = Integer.toString(currentPageIndex + 1);

        Map<String, String> pageNumToHref = paginationLinks
                .stream()
                .collect(toMap(Element::text, link -> link.attr(HREF_ATTR), pickFirstStrategy()));

        if (pageNumToHref.containsKey(nextPageIndex)) {
            return pageNumToHref.get(nextPageIndex);
        } else {
            return pageNumToHref.getOrDefault(NEXT_BLOCK_LINK, null);
        }
    }

    private List<ArticleContainer> collectContent(Elements articleLinks, int pageNumber) {
        return IntStream.range(0, articleLinks.size())
                .parallel()
                .mapToObj(index -> {
                    String url = articleLinks.get(index).attr(HREF_ATTR);
                    String content = pageContentProvider.getPageContent(url).html();
                    return new ArticleContainer(url, content, pageNumber, index);
                })
                .collect(Collectors.toList());
    }

}
