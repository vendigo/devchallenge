package it.devchallenge.tournament.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import it.devchallenge.tournament.model.match.MatchEntity;

public interface MatchRepository extends JpaRepository<MatchEntity, Long> {
}
