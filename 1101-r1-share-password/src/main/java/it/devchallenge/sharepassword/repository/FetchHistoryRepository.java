package it.devchallenge.sharepassword.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.devchallenge.sharepassword.model.FetchHistoryEntity;
import it.devchallenge.sharepassword.model.FetchStatus;

public interface FetchHistoryRepository extends JpaRepository<FetchHistoryEntity, Long> {

    List<FetchHistoryEntity> findByPasswordId(Long passwordId);

    List<FetchHistoryEntity> findByFetchStatus(FetchStatus fetchStatus);

    List<FetchHistoryEntity> findByCreatedDateBetween(LocalDateTime from, LocalDateTime to);
}
