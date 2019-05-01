package it.devchallenge.tournament.model.tournament;

import java.util.List;

import lombok.Value;

@Value
public class Tournament {
    String tournamentName;
    List<Round> rounds;
}
