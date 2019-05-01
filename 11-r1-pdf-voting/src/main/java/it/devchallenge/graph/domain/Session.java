package it.devchallenge.graph.domain;

import org.neo4j.ogm.annotation.*;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NodeEntity
@Data
@EqualsAndHashCode(exclude = "id")
@NoArgsConstructor
public class Session {
    @GraphId
    private Long id;
    @Property
    @Index(unique = true, primary = true)
    private String name;
    @Relationship(type = "CONDUCTED_IN")
    private Authority authority;

    public Session(String name, Authority authority) {
        this.name = name;
        this.authority = authority;
    }
}
