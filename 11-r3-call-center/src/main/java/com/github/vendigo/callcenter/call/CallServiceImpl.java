package com.github.vendigo.callcenter.call;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.*;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.vendigo.callcenter.employee.EmployeeRepository;
import com.github.vendigo.callcenter.matching.MatchingResult;
import com.github.vendigo.callcenter.matching.MatchingService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CallServiceImpl implements CallService {
    @Autowired
    private MatchingService matchingService;
    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public CallResponse handleCall(List<String> areas) {
        if (isEmpty(areas)) {
            return new CallResponse();
        }

        List<MatchingResult> matchingResults = matchingService.getMatchingResult(areas);
        Set<String> remainingEmployees = matchingResults.stream().map(MatchingResult::getEmployee).collect(toSet());
        Map<String, List<String>> expertiseToEmployee = groupByEmployeeSortByQualification(matchingResults);

        List<CallAssigment> assignments = new ArrayList<>();
        Set<String> assignedEmployees = new HashSet<>();
        for (String area : areas) {
            if (expertiseToEmployee.containsKey(area)) {
                Optional<String> suitableEmployee = expertiseToEmployee.get(area).stream()
                        .filter(remainingEmployees::contains)
                        .findFirst();
                if (suitableEmployee.isPresent()) {
                    String employeeName = suitableEmployee.get();
                    assignments.add(new CallAssigment(employeeName, area));
                    assignedEmployees.add(employeeName);
                    remainingEmployees.remove(employeeName);
                    continue;
                }
            }
            assignments.add(new CallAssigment(null, area));
        }

        deleteAssignedEmployees(assignedEmployees);
        return new CallResponse(assignedEmployees.size(), assignments);
    }

    @Transactional
    private void deleteAssignedEmployees(Set<String> assignedEmployees) {
        assignedEmployees.stream()
                .map(employeeRepository::findByName)
                .forEach(employeeRepository::delete);
    }

    private Map<String, List<String>> groupByEmployeeSortByQualification(List<MatchingResult> matchingResults) {
        Map<String, List<MatchingResult>> byExpertise = matchingResults.stream()
                .collect(groupingBy(MatchingResult::getExpertise));
        Map<String, List<String>> result = new HashMap<>();

        byExpertise.forEach((expertise, employees) -> {
            List<String> newList = employees.stream()
                    .sorted(getComparator())
                    .map(MatchingResult::getEmployee)
                    .collect(toList());
            result.put(expertise, newList);
        });

        return result;
    }

    static Comparator<MatchingResult> getComparator() {
        return comparing(MatchingResult::getQueryQualification)
                .thenComparing(MatchingResult::getOverallQualification);
    }

}
