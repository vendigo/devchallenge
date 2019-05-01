package it.devchallenge.sharepassword.controller;

import static it.devchallenge.sharepassword.model.FetchStatus.ALREADY_USED;
import static it.devchallenge.sharepassword.model.FetchStatus.EXPIRED;
import static it.devchallenge.sharepassword.model.FetchStatus.SUCCESS;
import static it.devchallenge.sharepassword.model.FetchStatus.VERIFICATION_FAILED;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import it.devchallenge.sharepassword.model.FetchHistoryEntity;
import it.devchallenge.sharepassword.model.FetchStatus;
import it.devchallenge.sharepassword.repository.FetchHistoryRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class FetchHistoryControllerIntTest {

    private static final LocalDateTime NOW = LocalDate.of(2018, Month.OCTOBER, 12).atStartOfDay();

    @Autowired
    private FetchHistoryRepository fetchHistoryRepository;
    @Autowired
    private FetchHistoryController fetchHistoryController;

    @Before
    public void setUp() {
        createHistory(1L, 1L, NOW, FetchStatus.WRONG_FORMAT);
        createHistory(2L, 1L, NOW.plusMinutes(1), VERIFICATION_FAILED);
        createHistory(3L, 1L, NOW.plusMinutes(2), SUCCESS);
        createHistory(4L, 1L, NOW.plusMinutes(3), ALREADY_USED);
        createHistory(5L, 2L, NOW.plusMinutes(4), SUCCESS);
        createHistory(6L, 2L, NOW.plusMinutes(5), ALREADY_USED);
        createHistory(7L, 3L, NOW.plusMinutes(6), EXPIRED);
    }

    @Test
    public void findByPasswordId() {
        List<FetchHistoryEntity> response = fetchHistoryController.getByPasswordId(1L);
        assertThat(response, hasSize(4));
    }

    @Test
    public void findByPasswordStatus() {
        List<FetchHistoryEntity> response = fetchHistoryController.getByStatus(FetchStatus.ALREADY_USED);
        assertThat(response, hasSize(2));
    }

    @Test
    public void findByPasswordDate() {
        List<FetchHistoryEntity> response = fetchHistoryController.getByDate(NOW.plusSeconds(61), NOW.plusMinutes(4));
        assertThat(response, hasSize(3));
    }

    private void createHistory(Long historyId, Long passwordId, LocalDateTime createdDate,
                                             FetchStatus status) {
        FetchHistoryEntity history = new FetchHistoryEntity();
        history.setId(historyId);
        history.setPasswordId(passwordId);
        history.setCreatedDate(createdDate);
        history.setFetchStatus(status);
        fetchHistoryRepository.save(history);
    }

}
