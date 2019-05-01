package com.github.vendigo.callcenter.employee;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.vendigo.callcenter.expertise.Expertise;
import com.github.vendigo.callcenter.expertise.ExpertiseRepository;

@RestController
public class EmployeeController {
    private static final String WELCOME_MESSAGE = "WELCOME";
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private ExpertiseRepository expertiseRepository;

    @GetMapping("register")
    public ResponseEntity<String> register(@RequestParam("name") String employeeName,
                                           @RequestParam("area")List<String> areas) {
        List<Expertise> expertise = areas.stream().map(Expertise::new).collect(Collectors.toList());
        employeeRepository.save(new Employee(employeeName, expertise));
        return new ResponseEntity<>(WELCOME_MESSAGE, HttpStatus.OK);
    }

    @GetMapping("reset")
    public ResponseEntity reset() {
        employeeRepository.deleteAll();
        expertiseRepository.deleteAll();
        return new ResponseEntity(HttpStatus.OK);
    }
}
