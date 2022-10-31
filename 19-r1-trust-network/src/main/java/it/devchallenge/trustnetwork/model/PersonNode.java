package it.devchallenge.trustnetwork.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.List;

@Node("Person")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonNode {
    @Id
    private String id;
    @Property
    private List<String> topics;
    @Relationship(type = "trust")
    private List<TrustedConnection> connections;

    public PersonNode(String id, List<String> topics) {
        this.id = id;
        this.topics = topics;
    }
}
