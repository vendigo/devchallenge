package com.github.vendigo.callcenter.call;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;

import org.junit.Test;

import com.github.vendigo.callcenter.matching.MatchingResult;

public class CallServiceTest {
    private static final MatchingResult MATCHING_RESULT_1 = new MatchingResult("exp1", "Emp1",
            1L, 4L);
    private static final MatchingResult MATCHING_RESULT_2 = new MatchingResult("exp1", "Emp2",
            1L, 2L);
    private static final MatchingResult MATCHING_RESULT_3 = new MatchingResult("exp1", "Emp3",
            2L, 2L);
    private static final MatchingResult MATCHING_RESULT_4 = new MatchingResult("exp1", "Emp4",
            1L, 1L);

    @Test
    public void comparator() throws Exception {
        List<MatchingResult> list = asList(MATCHING_RESULT_4, MATCHING_RESULT_3, MATCHING_RESULT_2,
                MATCHING_RESULT_1);
        List<MatchingResult> sorted = list.stream().sorted(CallServiceImpl.getComparator()).collect(toList());
        assertThat(sorted, equalTo(asList(MATCHING_RESULT_4, MATCHING_RESULT_2, MATCHING_RESULT_3,
                MATCHING_RESULT_1)));
    }
}
