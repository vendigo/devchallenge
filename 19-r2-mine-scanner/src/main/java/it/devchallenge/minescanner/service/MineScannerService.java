package it.devchallenge.minescanner.service;

import it.devchallenge.minescanner.model.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import one.util.streamex.EntryStream;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
@Slf4j
public class MineScannerService {

    private final ImageParserService imageParserService;
    private final GridParserService gridParserService;

    public ScanMinesResponse scanMines(ScanMinesRequest request) {
        log.info("Parsing image");
        ImagePixels imagePixels = imageParserService.parseImage(request.image());
        log.info("Parsing grid");
        Map<CellIndex, List<Double>> grid = gridParserService.parseGrid(imagePixels);
        log.info("Calculating cell levels");
        List<Mine> mines = EntryStream.of(grid)
                .parallel()
                .mapValues(this::cellLevel)
                .filterValues(level -> level >= request.minLevel())
                .mapKeyValue((index, level) -> new Mine(index.x(), index.y(), level))
                .sorted(Comparator.comparing(Mine::y).thenComparing(Mine::x))
                .toList();
        return new ScanMinesResponse(mines);
    }

    private int cellLevel(List<Double> pixels) {
        var avgDarkness = pixels.stream()
                .parallel()
                .mapToDouble(Double::valueOf)
                .summaryStatistics().getAverage();
        return (int) Math.round(avgDarkness);
    }
}
