package it.devchallenge.minescanner.service;

import it.devchallenge.minescanner.exception.InvalidInputException;
import it.devchallenge.minescanner.model.CellIndex;
import it.devchallenge.minescanner.model.ImagePixels;
import one.util.streamex.StreamEx;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class GridParserService {

    public static final double WHITE = 0.0;

    public Map<CellIndex, List<Double>> parseGrid(ImagePixels pixels) {
        GridSize gridSize = findGridSize(pixels);

        if (!gridSize.valid()) {
            throw new InvalidInputException("Invalid Grid detected");
        }

        return dividePixelsToCells(pixels, gridSize.gridSize());
    }

    private Map<CellIndex, List<Double>> dividePixelsToCells(ImagePixels pixels, int gridSize) {
        double[][] arr = pixels.pixels();
        List<GridPixel> cells = new ArrayList<>();

        for (int y = 0; y < pixels.height(); y++) {
            for (int x = 0; x < pixels.width(); x++) {
                Optional<Integer> xIndex = getCellIndex(gridSize, x);
                Optional<Integer> yIndex = getCellIndex(gridSize, y);
                if (xIndex.isPresent() && yIndex.isPresent()) {
                    cells.add(new GridPixel(new CellIndex(xIndex.get(), yIndex.get()), arr[y][x]));
                }
            }
        }

        return cells.stream()
                .collect(Collectors.groupingBy(GridPixel::cellIndex,
                        Collectors.mapping(GridPixel::color,
                                Collectors.toList())));
    }

    private GridSize findGridSize(ImagePixels pixels) {
        List<Integer> horizontalLines = horizontalLines(pixels);
        List<Integer> verticalLines = verticalLines(pixels);
        List<Integer> bigger = horizontalLines.size() > verticalLines.size() ? horizontalLines : verticalLines;
        List<Integer> smaller = horizontalLines.size() > verticalLines.size() ? verticalLines : horizontalLines;
        Integer biggerSide = Math.max(pixels.width(), pixels.height()) - 1;

        if (bigger.size() < 2 || bigger.get(0) != 0 || !Objects.equals(bigger.get(bigger.size() - 1), biggerSide)) {
            return new GridSize(false, 0);
        }

        List<Integer> biggerSubList = bigger.subList(0, smaller.size());
        if (!biggerSubList.equals(smaller)) {
            return new GridSize(false, 0);
        }

        List<Integer> diffs = StreamEx.of(bigger)
                .pairMap((l, r) -> r - l)
                .toList();
        Integer diff = diffs.get(0);
        boolean valid = diffs.stream().allMatch(d -> Objects.equals(d, diff));
        return new GridSize(valid, diff);
    }

    private List<Integer> horizontalLines(ImagePixels pixels) {
        return IntStream.range(0, pixels.height())
                .parallel()
                .filter(l -> isWhiteLine(pixels, l, true))
                .boxed()
                .toList();
    }

    private List<Integer> verticalLines(ImagePixels pixels) {
        return IntStream.range(0, pixels.width())
                .parallel()
                .filter(l -> isWhiteLine(pixels, l, false))
                .boxed()
                .toList();
    }

    private boolean isWhiteLine(ImagePixels pixels, int l, boolean isHorizontal) {
        int n = isHorizontal ? pixels.width() : pixels.height();
        double[][] arr = pixels.pixels();

        for (int i = 0; i < n; i++) {
            double pixel = isHorizontal ? arr[l][i] : arr[i][l];
            if (pixel != WHITE) {
                return false;
            }
        }

        return true;
    }

    private Optional<Integer> getCellIndex(int gridSize, int i) {
        if (i % gridSize == 0) {
            return Optional.empty();
        }
        return Optional.of(i / gridSize);
    }

    record GridSize(boolean valid, int gridSize) {
    }

    record GridPixel(CellIndex cellIndex, double color) {
    }
}
