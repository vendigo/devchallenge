package it.devchallenge.tournament.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import it.devchallenge.tournament.model.team.ParticipatingTeamEntity;

public interface ParticipatingTeamRepository extends JpaRepository<ParticipatingTeamEntity, Long> {
}
