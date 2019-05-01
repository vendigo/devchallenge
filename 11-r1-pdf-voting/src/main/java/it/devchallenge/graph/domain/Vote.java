package it.devchallenge.graph.domain;

import java.util.List;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NodeEntity
@Data
@EqualsAndHashCode(exclude = "id")
@NoArgsConstructor
public class Vote {
    @GraphId
    private Long id;
    @Property
    private String type;
    @Relationship(direction = "INCOMING")
    private List<Deputy> voted;

    public Vote(String type, List<Deputy> voted) {
        this.type = type;
        this.voted = voted;
    }
}
