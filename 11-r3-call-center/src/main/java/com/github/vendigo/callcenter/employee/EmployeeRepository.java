package com.github.vendigo.callcenter.employee;

import org.springframework.data.neo4j.repository.GraphRepository;

public interface EmployeeRepository extends GraphRepository<Employee> {
    Employee findByName(String name);
}
