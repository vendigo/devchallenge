package it.devchallenge.tournament.model.match;

import static javax.persistence.EnumType.STRING;

import javax.persistence.*;

import it.devchallenge.tournament.model.team.ParticipatingTeamEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "match")
@NoArgsConstructor
public class MatchEntity {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    @JoinColumn(name = "home_team_id")
    private ParticipatingTeamEntity homeTeam;
    @ManyToOne
    @JoinColumn(name = "guest_team_id")
    private ParticipatingTeamEntity guestTeam;
    @Column
    private Integer roundNum;
    @Column
    private Integer homeTeamGoals;
    @Column
    private Integer guestTeamGoals;
    @Enumerated(STRING)
    private MatchResultType matchResult;

    public MatchEntity(ParticipatingTeamEntity homeTeam, ParticipatingTeamEntity guestTeam, Integer roundNum) {
        this.homeTeam = homeTeam;
        this.guestTeam = guestTeam;
        this.roundNum = roundNum;
    }
}
