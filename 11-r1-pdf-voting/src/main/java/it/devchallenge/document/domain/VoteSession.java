package it.devchallenge.document.domain;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VoteSession {
    private String authorityName;
    private String sessionName;
    private String voteName;
    private String voteNumber;
    private Boolean voteSessionResult;
    private List<VoteEntry> voteEntries;
}
