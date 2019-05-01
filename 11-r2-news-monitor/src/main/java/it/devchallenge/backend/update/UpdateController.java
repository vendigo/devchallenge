package it.devchallenge.backend.update;

import static org.apache.commons.lang3.Validate.notNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import it.devchallenge.backend.config.ScheduleConfig;

/**
 * Controller for manual triggering update
 */
@RestController
public class UpdateController {
    private final ArticleUpdateManager updateManager;
    private final ScheduleConfig scheduleConfig;

    @Autowired
    public UpdateController(ArticleUpdateManager updateManager, ScheduleConfig scheduleConfig) {
        this.updateManager = notNull(updateManager);
        this.scheduleConfig = notNull(scheduleConfig);
    }

    @GetMapping("/manual/update")
    public ResponseEntity<String> update(@Param("pagesToUpdate") int pagesToUpdate) {
        int maxPagesToLoad = scheduleConfig.getMaxPagesToLoad();
        if (pagesToUpdate > maxPagesToLoad) {
            return new ResponseEntity<>("Provided pagesToUpdate parameter bigger than allowed",
                    HttpStatus.BAD_REQUEST);
        }
        updateManager.updateArticles(pagesToUpdate);
        return new ResponseEntity<>("Articles was synced", HttpStatus.OK);
    }
}
