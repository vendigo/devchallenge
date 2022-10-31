package it.devchallenge.minescanner.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ScanMinesRequest(@JsonProperty("min_level") int minLevel, String image) {
}
