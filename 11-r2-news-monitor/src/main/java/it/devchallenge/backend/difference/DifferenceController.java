package it.devchallenge.backend.difference;

import static org.apache.commons.lang3.Validate.notNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for displaying difference between two versions of the article.
 * Delegates to {@link DifferenceService}
 */
@RestController
public class DifferenceController {
    private final DifferenceService differenceService;

    @Autowired
    public DifferenceController(DifferenceService differenceService) {
        this.differenceService = notNull(differenceService);
    }

    @GetMapping("difference/")
    public String difference(@Param("leftId") long leftId, @Param("right") long rightId) {
        return differenceService.findDifference(leftId, rightId);
    }
}
