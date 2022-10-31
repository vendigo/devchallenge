package it.devchallenge.minescanner.controller;

import it.devchallenge.minescanner.model.ScanMinesRequest;
import it.devchallenge.minescanner.model.ScanMinesResponse;
import it.devchallenge.minescanner.service.MineScannerService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class MineScannerController {

    private final MineScannerService mineScannerService;

    @PostMapping("/api/image-input")
    public ScanMinesResponse scanMines(@RequestBody ScanMinesRequest request) {
        return mineScannerService.scanMines(request);
    }
}
