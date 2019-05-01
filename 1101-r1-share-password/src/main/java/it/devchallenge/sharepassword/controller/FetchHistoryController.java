package it.devchallenge.sharepassword.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.devchallenge.sharepassword.model.FetchHistoryEntity;
import it.devchallenge.sharepassword.model.FetchStatus;
import it.devchallenge.sharepassword.repository.FetchHistoryRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@AllArgsConstructor
@Slf4j
public class FetchHistoryController {

    private final FetchHistoryRepository fetchHistoryRepository;

    @GetMapping("/history/byId")
    public List<FetchHistoryEntity> getByPasswordId(@RequestParam("passwordId") Long passwordId) {
        log.info("Getting fetch history for passwordId: {}", passwordId);
        return fetchHistoryRepository.findByPasswordId(passwordId);
    }


    @GetMapping("/history/byStatus")
    public List<FetchHistoryEntity> getByStatus(@RequestParam("status") FetchStatus status) {
        log.info("Getting fetch history by status: {}", status);
        return fetchHistoryRepository.findByFetchStatus(status);
    }

    @GetMapping("/history/byDate")
    public List<FetchHistoryEntity> getByDate(@RequestParam("from") LocalDateTime from,
                                              @RequestParam("to") LocalDateTime to) {
        log.info("Getting fetch history between {} and {}", from, to);
        return fetchHistoryRepository.findByCreatedDateBetween(from, to);
    }
}
