package it.devchallenge.trustnetwork.model;

import java.util.List;

public record CreatePersonDto(String id, List<String> topics) {
}
