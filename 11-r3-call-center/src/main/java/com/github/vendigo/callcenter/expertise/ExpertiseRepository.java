package com.github.vendigo.callcenter.expertise;

import java.util.List;
import java.util.Map;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

public interface ExpertiseRepository extends GraphRepository<Expertise> {
    @Query("MATCH (exp:Expertise) WHERE exp.name IN {0}\n" +
            "MATCH (emp:Employee)-[:EXPERT_IN]->(exp)\n" +
            "MATCH (emp)-[overallQual:EXPERT_IN]->(:Expertise)\n" +
            "RETURN exp.name as expertise, emp.name as employee, count(overallQual) as overallQualification")
    List<Map<String, Object>> getMatchingResults(List<String> areas);
}
