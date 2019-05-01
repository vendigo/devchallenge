package com.github.vendigo.callcenter.employee;

import java.util.List;

import org.neo4j.ogm.annotation.*;

import com.github.vendigo.callcenter.expertise.Expertise;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NodeEntity
@Data
@EqualsAndHashCode(exclude = "id")
@NoArgsConstructor
public class Employee {
    @GraphId
    private Long id;
    @Property
    @Index(unique = true, primary = true)
    private String name;
    @Relationship(type = "EXPERT_IN")
    private List<Expertise> expertise;

    public Employee(String name, List<Expertise> expertise) {
        this.name = name;
        this.expertise = expertise;
    }
}
