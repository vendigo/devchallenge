package it.devchallenge.backend.difference;

/**
 * Finds difference between two snapshots
 */
public interface DifferenceService {
    /**
     * @param leftId - id of left snapshot
     * @param rightId - id of right snapshot
     * @return - difference in html form
     */
    String findDifference(long leftId, long rightId);
}
