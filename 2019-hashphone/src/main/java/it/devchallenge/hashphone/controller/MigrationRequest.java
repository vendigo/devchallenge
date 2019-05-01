package it.devchallenge.hashphone.controller;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class MigrationRequest {

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String algorithm;
    private String salt;
}
