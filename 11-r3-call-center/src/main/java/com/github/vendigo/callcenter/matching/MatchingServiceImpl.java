package com.github.vendigo.callcenter.matching;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.*;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.vendigo.callcenter.expertise.ExpertiseRepository;

@Service
public class MatchingServiceImpl implements MatchingService {
    private static final String EXPERTISE = "expertise";
    private static final String EMPLOYEE = "employee";
    private static final String OVERALL_QUALIFICATION = "overallQualification";
    @Autowired
    private ExpertiseRepository expertiseRepository;

    @Override
    public List<MatchingResult> getMatchingResult(List<String> areas) {
        List<Map<String, Object>> rawResults = expertiseRepository.getMatchingResults(areas);
        Map<String, Long> queryQualification = rawResults.stream()
                .map(raw -> (String) raw.get(EMPLOYEE))
                .collect(groupingBy(identity(), counting()));

        return rawResults.stream().map(raw -> new MatchingResult((String) raw.get(EXPERTISE),
                (String) raw.get(EMPLOYEE),
                (Long) raw.get(OVERALL_QUALIFICATION),
                queryQualification.get(raw.get(EMPLOYEE))))
                .collect(toList());
    }

}
