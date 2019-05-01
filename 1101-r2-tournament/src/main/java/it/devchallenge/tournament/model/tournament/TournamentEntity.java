package it.devchallenge.tournament.model.tournament;

import java.util.List;

import javax.persistence.*;

import it.devchallenge.tournament.model.match.MatchEntity;
import lombok.Data;

@Data
@Entity
@Table(name = "tournament")
public class TournamentEntity {
    @Id
    @GeneratedValue
    private Long id;
    @Column
    private String name;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "tournament_id")
    private List<MatchEntity> matches;
}
