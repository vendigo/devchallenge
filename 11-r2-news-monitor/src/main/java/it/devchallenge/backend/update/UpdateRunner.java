package it.devchallenge.backend.update;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.Validate.notNull;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.stereotype.Component;

import it.devchallenge.backend.config.ExecTime;
import it.devchallenge.backend.config.ScheduleConfig;
import lombok.extern.slf4j.Slf4j;

/**
 * Runs once per hour and checks whether update is required according to {@link ScheduleConfig}
 */
@Slf4j
@Component
@Profile("prod")
public class UpdateRunner {
    private final ScheduleConfig scheduleConfig;
    private final ArticleUpdateManager updateManager;

    @Autowired
    public UpdateRunner(ScheduleConfig scheduleConfig, ArticleUpdateManager updateManager) {
        this.scheduleConfig = notNull(scheduleConfig);
        this.updateManager = notNull(updateManager);
    }

    @PostConstruct
    public void initialLoad() {
        int pagesToLoad = scheduleConfig.getMaxPagesToLoad();
        log.info("Loading {} pages", pagesToLoad);
        updateManager.updateArticles(pagesToLoad);
    }

    @Scheduled(cron = "0 0 * * * *")
    public void runUpdate() {
        List<ExecTime> execTimes = scheduleConfig.getExecTimes().stream()
                .sorted(comparing(ExecTime::getPagesToSync).reversed())
                .collect(toList());

        LocalDateTime now = LocalDateTime.now();
        for (ExecTime execTime : execTimes) {
            LocalDateTime nextDate = getNextDate(execTime.getCron());
            if (roughEqual(now, nextDate)) {
                updateManager.updateArticles(execTime.getPagesToSync());
                return;
            }
        }
    }

    private LocalDateTime getNextDate(String cron) {
        CronTrigger cronTrigger = new CronTrigger(cron);
        Date nextExecutionTime = cronTrigger.nextExecutionTime(new SimpleTriggerContext());
        return LocalDateTime.ofInstant(nextExecutionTime.toInstant(), ZoneId.systemDefault());
    }

    private boolean roughEqual(LocalDateTime first, LocalDateTime second) {
        return first.toLocalDate().equals(second.toLocalDate()) &&
                first.getHour() == second.getHour() &&
                first.getMinute() == second.getMinute();
    }
}
