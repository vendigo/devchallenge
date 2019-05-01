package it.devchallenge.api.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.annotation.Depth;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

import it.devchallenge.graph.domain.Project;

public interface ProjectRepository extends Repository<Project, Long> {
    @RestResource(exported = false)
    void save(List<Project> records);
    Page<Project> findAll(Pageable pageable);
    Project findOne(Long id);
    Project findOne(Long id, int depth);

    @Depth(2)
    @RestResource(path = "byName")
    Page<Project> findByNameLike(@Param("name") String name, Pageable pageable);
    @Depth(2)
    @RestResource(path = "bySessionName")
    Page<Project> findBySessionNameLike(@Param("name") String name, Pageable pageable);
}
