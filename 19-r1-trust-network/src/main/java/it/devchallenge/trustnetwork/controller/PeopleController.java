package it.devchallenge.trustnetwork.controller;

import it.devchallenge.trustnetwork.model.CreatePersonDto;
import it.devchallenge.trustnetwork.service.PeopleService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(path = "/api/people")
@ResponseStatus(HttpStatus.CREATED)
@AllArgsConstructor
@Slf4j
public class PeopleController {

    private final PeopleService peopleService;

    @PostMapping
    public CreatePersonDto createPerson(@RequestBody CreatePersonDto createPersonDto) {
        log.info("Creating person: {}", createPersonDto.id());
        peopleService.createPerson(createPersonDto);
        return createPersonDto;
    }

    @PostMapping("/{personId}/trust_connections")
    public void createConnection(@PathVariable("personId") String personId, @RequestBody Map<String, Integer> connections) {
        log.info("Creating connections from: {} to: {}", personId, connections);
        peopleService.createAndUpdateConnections(personId, connections);
    }
}
