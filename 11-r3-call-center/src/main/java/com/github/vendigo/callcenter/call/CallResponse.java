package com.github.vendigo.callcenter.call;

import java.util.Collections;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CallResponse {
    private int totalAssignments;
    private List<CallAssigment> assignments = Collections.emptyList();
}
