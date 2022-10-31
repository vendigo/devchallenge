package it.devchallenge.trustnetwork.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.*;

@RelationshipProperties
@Data
@NoArgsConstructor
public class TrustedConnection {

    @Id
    @GeneratedValue
    private Long id;
    @Property
    private Integer trustLevel;
    @TargetNode
    private PersonNode person;

    public TrustedConnection(Integer trustLevel, PersonNode person) {
        this.trustLevel = trustLevel;
        this.person = person;
    }
}
