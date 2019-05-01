package it.devchallenge.tournament.model.tournament;

import java.util.List;

import it.devchallenge.tournament.model.team.TeamResult;
import lombok.Value;

@Value
public class TournamentResults {
    List<Round> schedule;
    List<TeamResult> teamResults;
}
