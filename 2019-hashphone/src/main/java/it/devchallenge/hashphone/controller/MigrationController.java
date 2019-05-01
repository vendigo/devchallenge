package it.devchallenge.hashphone.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import it.devchallenge.hashphone.service.MigrationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@AllArgsConstructor
@Slf4j
public class MigrationController {

    private final MigrationService migrationService;

    @PostMapping("/migration")
    public ResponseEntity<String> createMigration(@RequestBody MigrationRequest migrationRequest) {
        log.info("Creating migration: {}", migrationRequest);
        migrationService.createMigration(migrationRequest);
        return ResponseEntity.ok().build();
    }
}
