package it.devchallenge.graph.domain;

import java.util.List;

import org.neo4j.ogm.annotation.*;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NodeEntity
@Data
@EqualsAndHashCode(exclude = "id")
@NoArgsConstructor
public class Project {
    @GraphId
    private Long id;
    @Property
    @Index
    private String name;
    @Property
    private String number;
    @Property
    private Boolean accepted;
    @Relationship(type = "PART_OF")
    private Session session;
    @Relationship(direction = "INCOMING", type = "VOTE_FOR")
    private List<Vote> votes;

    public Project(String name, String number, Boolean accepted, Session session, List<Vote> votes) {
        this.name = name;
        this.number = number;
        this.accepted = accepted;
        this.session = session;
        this.votes = votes;
    }
}
