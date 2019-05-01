package it.devchallenge.tournament.model.tournament;

import java.util.List;

import it.devchallenge.tournament.model.match.Match;
import lombok.Value;

@Value
public class Round {
    int roundNum;
    List<Match> matches;
}
