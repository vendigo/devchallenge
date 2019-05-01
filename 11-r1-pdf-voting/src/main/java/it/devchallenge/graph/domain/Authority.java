package it.devchallenge.graph.domain;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.Index;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NodeEntity
@Data
@EqualsAndHashCode(exclude = "id")
@NoArgsConstructor
public class Authority {
    @GraphId
    private Long id;
    @Property
    @Index(unique = true, primary = true)
    private String name;

    public Authority(String name) {
        this.name = name;
    }
}
