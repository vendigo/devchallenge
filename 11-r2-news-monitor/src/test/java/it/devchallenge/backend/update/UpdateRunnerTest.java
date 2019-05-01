package it.devchallenge.backend.update;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import it.devchallenge.backend.config.ExecTime;
import it.devchallenge.backend.config.ScheduleConfig;

@RunWith(MockitoJUnitRunner.class)
public class UpdateRunnerTest {
    @Mock
    private ArticleUpdateManager updateManager;
    private UpdateRunner updateRunner;
    private static final ExecTime EACH_HOUR = new ExecTime(3, "0 0 * * * *");
    private static final ExecTime EACH_THREE_HOURS = new ExecTime(10, "0 0 0/3 * * *");
    private static final ExecTime ONCE_A_DAY = new ExecTime(50, "0 0 0 * * *");
    private static final ScheduleConfig SCHEDULE_CONFIG = new ScheduleConfig(asList(EACH_HOUR, EACH_THREE_HOURS,
            ONCE_A_DAY));

    @Before
    public void setUp() throws Exception {
        updateRunner = new UpdateRunner(SCHEDULE_CONFIG, updateManager);
    }

    @Test
    public void initialLoadTakeMaxPages() throws Exception {
        updateRunner.initialLoad();
        verify(updateManager).updateArticles(50);
    }
}
