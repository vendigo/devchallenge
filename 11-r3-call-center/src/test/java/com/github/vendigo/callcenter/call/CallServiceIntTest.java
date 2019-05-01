package com.github.vendigo.callcenter.call;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.github.vendigo.callcenter.employee.Employee;
import com.github.vendigo.callcenter.employee.EmployeeRepository;
import com.github.vendigo.callcenter.expertise.Expertise;
import com.github.vendigo.callcenter.expertise.ExpertiseRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CallServiceIntTest {
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private ExpertiseRepository expertiseRepository;
    @Autowired
    private CallService callService;

    @Before
    public void setUp() throws Exception {
        employeeRepository.deleteAll();
        expertiseRepository.deleteAll();
    }

    @Test
    public void callWithEmptyArgument() {
        assertThat(callService.handleCall(Collections.emptyList()), equalTo(new CallResponse()));
    }

    @Test
    public void foundOne() {
        Expertise exp1 = new Expertise("exp 1");
        Employee employee1 = new Employee("Employee1", singletonList(exp1));
        employeeRepository.save(employee1);

        CallResponse actualResponse = callService.handleCall(singletonList("exp 1"));
        assertThat(actualResponse, equalTo(new CallResponse(1,
                singletonList(new CallAssigment("Employee1", "exp 1")))));
    }

    @Test
    public void employeeUsedOnlyOnceInOneBatch() {
        Expertise exp1 = new Expertise("exp 1");
        Employee employee1 = new Employee("Employee1", singletonList(exp1));
        employeeRepository.save(employee1);

        CallResponse actualResponse = callService.handleCall(asList("exp 1", "exp 1", "exp 1"));
        assertThat(actualResponse, equalTo(new CallResponse(1,
                asList(new CallAssigment("Employee1", "exp 1"),
                        new CallAssigment(null, "exp 1"),
                        new CallAssigment(null, "exp 1")))));
    }

    @Test
    public void employeeUsedOnlyOnceInDifferentBatches() {
        Expertise exp1 = new Expertise("exp 1");
        Employee employee1 = new Employee("Employee1", singletonList(exp1));
        employeeRepository.save(employee1);

        CallResponse response1 = callService.handleCall(singletonList("exp 1"));
        CallResponse response2 = callService.handleCall(singletonList("exp 1"));
        assertThat(response1, equalTo(new CallResponse(1,
                singletonList(new CallAssigment("Employee1", "exp 1")))));
        assertThat(response2, equalTo(new CallResponse(0,
                singletonList(new CallAssigment(null, "exp 1")))));
    }

    @Test
    public void searchNonExistentExpertise() {
        Expertise exp1 = new Expertise("exp 1");
        Employee employee1 = new Employee("Employee1", singletonList(exp1));
        employeeRepository.save(employee1);

        CallResponse actualResponse = callService.handleCall(singletonList("exp 2"));
        assertThat(actualResponse, equalTo(new CallResponse(0,
                singletonList(new CallAssigment(null, "exp 2")))));
    }

    @Test
    public void lessQualifiedEmployeePicked() {
        Expertise exp1 = new Expertise("exp 1");
        Expertise exp2 = new Expertise("exp 2");
        Expertise exp3 = new Expertise("exp 3");

        Employee employee1 = new Employee("Employee1", asList(exp1, exp2, exp3));
        Employee employee2 = new Employee("Employee2", singletonList(exp1));
        employeeRepository.save(employee1);
        employeeRepository.save(employee2);

        CallResponse actualResponse = callService.handleCall(singletonList("exp 1"));
        assertThat(actualResponse, equalTo(new CallResponse(1,
                singletonList(new CallAssigment("Employee2", "exp 1")))));
    }

    @Test
    public void pickIsOptimalWithinBatch() {
        Expertise exp1 = new Expertise("exp 1");
        Expertise exp2 = new Expertise("exp 2");
        Expertise exp3 = new Expertise("exp 3");
        Expertise exp4 = new Expertise("exp 4");

        Employee employee1 = new Employee("Employee1", asList(exp1, exp2));
        Employee employee2 = new Employee("Employee2", asList(exp1, exp3, exp4));
        employeeRepository.save(employee1);
        employeeRepository.save(employee2);

        CallResponse actualResponse = callService.handleCall(asList("exp 1", "exp 2"));
        assertThat(actualResponse, equalTo(new CallResponse(2,
                asList(new CallAssigment("Employee2", "exp 1"),
                        new CallAssigment("Employee1", "exp 2")))));
    }
}
