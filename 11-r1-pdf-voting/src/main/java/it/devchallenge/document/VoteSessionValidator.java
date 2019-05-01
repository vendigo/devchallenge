package it.devchallenge.document;

import static org.springframework.util.CollectionUtils.isEmpty;
import static org.springframework.util.StringUtils.hasText;

import it.devchallenge.document.domain.VoteSession;

public class VoteSessionValidator {
    public static boolean isValid(VoteSession v) {
        return hasText(v.getAuthorityName()) &&
                hasText(v.getSessionName()) &&
                hasText(v.getVoteName()) &&
                hasText(v.getVoteNumber()) &&
                !isEmpty(v.getVoteEntries());
    }
}
