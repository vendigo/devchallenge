package com.github.vendigo.callcenter.matching;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MatchingResult {
    private String expertise;
    private String employee;
    private Long overallQualification;
    private Long queryQualification;
}
