package it.devchallenge.document;

import static java.util.Arrays.asList;

import java.util.*;
import java.util.regex.Pattern;

import it.devchallenge.document.domain.VoteResult;

class ParseUtils {
    private static final String POSITIVE_RESULT = "ПРИЙНЯТО";
    private static final String NEGATIVE_RESULT = "НЕ ПРИЙНЯТО";
    private static final Map<String, VoteResult> voteResultMap;
    static {
        Map<String, VoteResult> resultMap = new HashMap<>();
        resultMap.put("За", VoteResult.IN_FAVOR);
        resultMap.put("Проти", VoteResult.AGAINST);
        resultMap.put("Відсутній", VoteResult.ABSENT);
        resultMap.put("Утримався", VoteResult.ABSTAINED);
        voteResultMap = Collections.unmodifiableMap(resultMap);
    }

    private ParseUtils() {
    }

    static List<String> splitToList(String text, String regex) {
        return new ArrayList<>(asList(text.split(regex)));
    }

    static VoteResult parseVoteResult(String textValue) {
        return voteResultMap.get(textValue);
    }

    static Optional<String> findByPattern(List<String> lines, Pattern pattern) {
        return lines.stream()
                .filter(pattern.asPredicate())
                .findFirst();
    }

    static Optional<Integer> findNumberByPattern(List<String> lines, Pattern pattern) {
        int num = 0;
        for (String line : lines) {
            if (pattern.matcher(line).find()) {
                return Optional.of(num);
            }
            num++;
        }
        return Optional.empty();
    }

    static Boolean parseVoteSessionResult(String s) {
        if (POSITIVE_RESULT.equals(s)) {
            return Boolean.TRUE;
        } else if (NEGATIVE_RESULT.equals(s)) {
            return Boolean.FALSE;
        }
        return null;
    }
}
