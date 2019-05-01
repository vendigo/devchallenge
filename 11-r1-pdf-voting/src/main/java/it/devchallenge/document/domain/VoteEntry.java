package it.devchallenge.document.domain;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class VoteEntry {
    private final String deputyName;
    private final VoteResult voteResult;
}
