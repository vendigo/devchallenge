package it.devchallenge.trustnetwork.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record MessageRequestDto(String text, List<String> topics,
                                @JsonProperty("from_person_id") String fromPersonId,
                                @JsonProperty("min_trust_level") Integer minTrustLevel) {
}
