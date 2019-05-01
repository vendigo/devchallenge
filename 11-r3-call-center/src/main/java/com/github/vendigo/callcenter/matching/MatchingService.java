package com.github.vendigo.callcenter.matching;

import java.util.List;

public interface MatchingService {
    List<MatchingResult> getMatchingResult(List<String> areas);
}
