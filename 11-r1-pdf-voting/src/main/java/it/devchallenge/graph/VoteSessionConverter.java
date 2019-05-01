package it.devchallenge.graph;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import it.devchallenge.document.domain.VoteEntry;
import it.devchallenge.document.domain.VoteSession;
import it.devchallenge.graph.domain.*;

@Component
public class VoteSessionConverter {
    public Project convert(VoteSession vs) {
        Authority authority = new Authority(vs.getAuthorityName());
        Session session = new Session(vs.getSessionName(), authority);
        List<Vote> votes = new ArrayList<>();
        vs.getVoteEntries()
                .stream()
                .collect(groupingBy(VoteEntry::getVoteResult))
                .forEach(((voteType, voteEntries) -> {
                    List<Deputy> deputies = voteEntries.stream()
                            .map(VoteEntry::getDeputyName)
                            .map(Deputy::new)
                            .collect(toList());
                    votes.add(new Vote(voteType.toString(), deputies));
                }));
        return new Project(vs.getVoteName(), vs.getVoteNumber(), vs.getVoteSessionResult(), session, votes);
    }
}
