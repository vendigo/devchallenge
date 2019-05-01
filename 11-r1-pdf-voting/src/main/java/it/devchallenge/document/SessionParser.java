package it.devchallenge.document;

import static it.devchallenge.document.ParseUtils.*;
import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import it.devchallenge.document.domain.VoteEntry;
import it.devchallenge.document.domain.VoteSession;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SessionParser {
    private static final String PAGE_BEGIN = "Система поіменного голосування \"Рада Голос\"";
    private static final int AUTHORITY_NAME_INDEX = 1;
    private static final int SESSION_NAME_INDEX = 2;
    private static final Pattern VOTE_NAME_HEADER_PATTERN = Pattern.compile("Результат поіменного голосування:");
    private static final Pattern NUM_LINE_PATTERN = Pattern.compile("№: (.+)");
    private static final Pattern RESULT_LINE_PATTERN = Pattern.compile("Рішення: (ПРИЙНЯТО|НЕ ПРИЙНЯТО)");
    private static final int FIRST_GROUP = 1;
    private static final int SECOND_GROUP = 2;
    private static final String SPACE = " ";
    private static final Pattern VOTE_BLOCK_PATTERN = Pattern.compile("Результат\\s+голосування\\s+1(.+)" +
            "ПІДСУМКИ ГОЛОСУВАННЯ", Pattern.DOTALL);
    private static final Pattern VOTE_ENTRY_PATTERN = Pattern.compile("(\\W+) (За|Проти|Відсутній|Утримався)");
    @Value("${line.separator}")
    private String lineSeparator;

    Stream<VoteSession> parseSessions(String text) {
        return splitToList(text, PAGE_BEGIN)
                .parallelStream()
                .skip(1)
                .map(this::parseOne)
                .filter(VoteSessionValidator::isValid);
    }

    private VoteSession parseOne(String page) {
        List<String> lines = splitToList(page, lineSeparator);
        VoteSession.VoteSessionBuilder builder = VoteSession.builder()
                .authorityName(lines.get(AUTHORITY_NAME_INDEX))
                .sessionName(lines.get(SESSION_NAME_INDEX));
        parseVoteName(lines, builder);
        parseNumLine(lines, builder);
        parseResultLine(lines, builder);
        parseVoteEntries(page, builder);

        return builder
                .build();
    }

    private void parseVoteEntries(String page, VoteSession.VoteSessionBuilder builder) {
        Matcher matcher = VOTE_BLOCK_PATTERN.matcher(page);
        if (matcher.find()) {
            String voteBlock = matcher.group(FIRST_GROUP);
            voteBlock = voteBlock.replace(lineSeparator, SPACE);
            List<VoteEntry> voteEntries = parseVoteBlock(voteBlock);
            builder.voteEntries(voteEntries);
        }
    }

    private List<VoteEntry> parseVoteBlock(String voteBlock) {
        Matcher matcher = VOTE_ENTRY_PATTERN.matcher(voteBlock);
        List<VoteEntry> result = new ArrayList<>();

        while (matcher.find()) {
            String name = matcher.group(FIRST_GROUP);
            String vote = matcher.group(SECOND_GROUP);
            result.add(new VoteEntry(name.trim(), parseVoteResult(vote)));
        }

        return result;
    }

    private void parseVoteName(List<String> lines, VoteSession.VoteSessionBuilder builder) {
        Optional<Integer> headerLineNumber = findNumberByPattern(lines, VOTE_NAME_HEADER_PATTERN);
        Optional<Integer> numLineNumber = findNumberByPattern(lines, NUM_LINE_PATTERN);
        if (headerLineNumber.isPresent() && numLineNumber.isPresent()) {
            String voteName = lines
                    .subList(headerLineNumber.get() + 1, numLineNumber.get())
                    .stream()
                    .collect(joining(SPACE));
            builder.voteName(voteName);
        }
    }

    private void parseResultLine(List<String> lines, VoteSession.VoteSessionBuilder builder) {
        Optional<String> resultLine = findByPattern(lines, RESULT_LINE_PATTERN);

        if (resultLine.isPresent()) {
            Matcher matcher = RESULT_LINE_PATTERN.matcher(resultLine.get());
            if (matcher.find()) {
                builder.voteSessionResult(parseVoteSessionResult(matcher.group(FIRST_GROUP)));
            }
        }
    }

    private void parseNumLine(List<String> lines, VoteSession.VoteSessionBuilder builder) {
        Optional<String> numLine = findByPattern(lines, NUM_LINE_PATTERN);

        if (numLine.isPresent()) {
            Matcher matcher = NUM_LINE_PATTERN.matcher(numLine.get());
            if (matcher.find()) {
                builder.voteNumber(matcher.group(FIRST_GROUP));
            }
        }
    }
}
