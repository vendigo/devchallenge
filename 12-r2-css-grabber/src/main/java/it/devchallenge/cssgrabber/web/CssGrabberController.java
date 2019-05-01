package it.devchallenge.cssgrabber.web;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import it.devchallenge.cssgrabber.cache.CacheService;
import it.devchallenge.cssgrabber.css.CssGrabberService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;

@RestController
@AllArgsConstructor
@Slf4j
public class CssGrabberController {

    private final CssGrabberService cssGrabberService;
    private final CacheService cacheService;

    @PostMapping("styles")
    public Mono<Map<String, String>> styles(@RequestBody List<String> request) {
        log.info("Getting styles for urls: {}", request);
        return Flux.fromIterable(request)
            .flatMap(this::executeAsync)
            .collectMap(Tuple2::getT1, Tuple2::getT2);
    }

    @PostMapping("cache/invalidate")
    public void invalidateWholeCache() {
        cacheService.invalidateCache();
    }

    @PostMapping("cache/invalidate/url")
    public void invalidateForUrl(@RequestBody String url) {
        cacheService.invalidateCache(url);
    }

    private Mono<Tuple2<String, String>> executeAsync(String url) {
        return Mono.zip(
            Mono.just(url),
            Mono.fromCallable(() -> cssGrabberService.getCss(url))
                .subscribeOn(Schedulers.elastic())
        );
    }
}
