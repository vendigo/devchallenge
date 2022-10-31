package it.devchallenge.trustnetwork.controller;

import it.devchallenge.trustnetwork.model.MessageRequestDto;
import it.devchallenge.trustnetwork.model.PathResponseDto;
import it.devchallenge.trustnetwork.service.MessagesService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/api")
@AllArgsConstructor
@ResponseStatus(HttpStatus.CREATED)
@Slf4j
public class MessageController {

    private final MessagesService messagesService;

    @PostMapping("/messages")
    public Map<String, List<String>> messages(@RequestBody MessageRequestDto messageRequest) {
        log.info("Sending messages: {}", messageRequest);
        return messagesService.messages(messageRequest);
    }

    @PostMapping("/path")
    public PathResponseDto path(@RequestBody MessageRequestDto pathRequest) {
        log.info("Searching path: {}", pathRequest);
        List<String> path = messagesService.shortestPath(pathRequest);
        return new PathResponseDto(pathRequest.fromPersonId(), path);
    }
}
