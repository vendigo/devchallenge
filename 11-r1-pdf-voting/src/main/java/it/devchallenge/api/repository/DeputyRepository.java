package it.devchallenge.api.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import it.devchallenge.graph.domain.Deputy;

@RepositoryRestResource(exported = false)
public interface DeputyRepository extends Repository<Deputy, Long> {
    @Query("match (d1:Deputy)\n" +
            "match (d2:Deputy)\n" +
            "match p=(d1)-[:VOTED]->(v:Vote)<-[:VOTED]-(d2)\n" +
            "where d1.name > d2.name\n" +
            "return distinct d1.name as first, d2.name as second, count(p) as sameVotes order by sameVotes desc limit {0}")
    List<Map<String, Object>> findClosestDeputies(int count);

    @Query("match (d1:Deputy{name: {0}})\n" +
            "match (d2:Deputy)\n" +
            "match p=(d1)-[:VOTED]->(v:Vote)<-[:VOTED]-(d2)\n" +
            "where d1.name > d2.name\n" +
            "return distinct d2.name as name, count(p) as sameVotes order by sameVotes desc limit {1}")
    List<Map<String, Object>> findClosestToDeputy(String name, int count);

    @Query("match (d1:Deputy{name: {0}})\n" +
            "match (d2:Deputy)\n" +
            "match p=(d1)-[:VOTED]->(v:Vote)<-[:VOTED]-(d2)\n" +
            "where d1.name > d2.name\n" +
            "return distinct d2.name as name, count(p) as sameVotes order by sameVotes limit {1}")
    List<Map<String, Object>> findFarthestToDeputy(String name, int count);
}
