package it.devchallenge.backend.difference;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.devchallenge.backend.snapshot.Snapshot;
import it.devchallenge.backend.snapshot.SnapshotRepository;
import name.fraser.neil.plaintext.diff_match_patch;

/**
 * Implementation based on google diff match patch library
 */
@Component
public class DifferenceServiceImpl implements DifferenceService {
    private final SnapshotRepository snapshotRepository;

    @Autowired
    public DifferenceServiceImpl(SnapshotRepository snapshotRepository) {
        this.snapshotRepository = notNull(snapshotRepository);
    }

    @Override
    public String findDifference(long leftId, long rightId) {
        Snapshot left = snapshotRepository.findOne(leftId);
        Snapshot right = snapshotRepository.findOne(rightId);
        Optional<String> resultMessage = validateSnapshots(left, right);
        if (resultMessage.isPresent()) {
            return resultMessage.get();
        }

        diff_match_patch diffProcessor = new diff_match_patch();
        return diffProcessor.diff_prettyHtml(diffProcessor.diff_main(left.getContent(),
                right.getContent()));
    }

    private Optional<String> validateSnapshots(Snapshot left, Snapshot right) {
        if (left == null) {
            return Optional.of("Left snapshot doesn't exist");
        }
        if (right == null) {
            return Optional.of("Right snapshot doesn't exist");
        }

        String leftContent = left.getContent();
        if (leftContent == null) {
            return Optional.of("Left snapshot doesn't have content");
        }

        String rightContent = right.getContent();
        if (rightContent == null) {
            return Optional.of("Right snapshot doesn't have content");
        }

        if (leftContent.equals(rightContent)) {
            return Optional.of("Snapshots have equal content");
        }

        return Optional.empty();
    }
}
