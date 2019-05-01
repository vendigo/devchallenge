package it.devchallenge.tournament.model.team;

import javax.persistence.*;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "participating_team")
@NoArgsConstructor
public class ParticipatingTeamEntity {
    @Id
    @GeneratedValue
    private Long id;
    @Column
    private String name;

    public ParticipatingTeamEntity(String name) {
        this.name = name;
    }
}
