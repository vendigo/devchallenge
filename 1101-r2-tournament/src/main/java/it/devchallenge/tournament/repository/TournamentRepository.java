package it.devchallenge.tournament.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import it.devchallenge.tournament.model.tournament.TournamentEntity;

public interface TournamentRepository extends JpaRepository<TournamentEntity, Long> {
    Optional<TournamentEntity> findByName(String name);
}
