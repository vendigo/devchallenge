package it.devchallenge.hashphone.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.devchallenge.hashphone.service.HashingService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RestController
@AllArgsConstructor
@Slf4j
public class HashPhoneController {

    private final HashingService hashingService;

    @PostMapping("/hash")
    public Mono<String> hashPhone(@RequestBody String phoneNumber) {
        return hashingService.hashPhoneNumber(phoneNumber);
    }

    @GetMapping("/find")
    public Mono<String> findPhone(@RequestParam("hashValue") String hashValue) {
        return hashingService.findPhoneNumber(hashValue);
    }

    @PostMapping("/convert")
    public Mono<String> convertHashValue(@RequestBody String oldHashValue) {
        log.info("Converting old hash {} to new format", oldHashValue);
        return hashingService.migrateHash(oldHashValue);
    }
}
