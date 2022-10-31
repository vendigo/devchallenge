package it.devchallenge.trustnetwork.repository;

import it.devchallenge.trustnetwork.model.PersonNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface PeopleRepository extends Neo4jRepository<PersonNode, String> {

    @Query("""
            MATCH p=(:Person{id: $from})-[:trust*]->(:Person) WHERE
                        all(r IN relationships(p) WHERE r.trustLevel >=$minTrustLevel) AND
                        all(pr IN nodes(p) WHERE pr.id = $from OR all(requiredTopic IN $requiredTopics WHERE requiredTopic IN pr.topics))
                        RETURN {idFrom: nodes(p)[-2].id, idTo: nodes(p)[-1].id}
            """)
    List<Map<String, String>> findDirectMessages(@Param("from") String from, @Param("minTrustLevel") Integer minTrustLevel,
                                                 @Param("requiredTopics") List<String> requiredTopics);

    @Query("""
            MATCH p=shortestPath((:Person{id: $from})-[:trust*]->(l:Person)) WHERE
                        all(r IN relationships(p) WHERE r.trustLevel >= $minTrustLevel) AND
                        all(requiredTopic IN $requiredTopics WHERE requiredTopic IN l.topics)
                        RETURN p ORDER BY length(p) LIMIT 1
            """)
    List<PersonNode> findShortestPath(@Param("from") String from, @Param("minTrustLevel") Integer minTrustLevel,
                                                 @Param("requiredTopics") List<String> requiredTopics);

    @Query("MATCH (:Person{id: $id})-[t:trust]->(:Person) DELETE t")
    void deleteConnections(@Param("id") String id);
}
