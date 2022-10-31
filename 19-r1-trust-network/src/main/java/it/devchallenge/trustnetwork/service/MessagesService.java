package it.devchallenge.trustnetwork.service;

import it.devchallenge.trustnetwork.model.MessageRequestDto;
import it.devchallenge.trustnetwork.model.PersonNode;
import it.devchallenge.trustnetwork.repository.PeopleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MessagesService {

    private final PeopleRepository peopleRepository;
    private final ValidationService validationService;

    public Map<String, List<String>> messages(MessageRequestDto messageRequest) {
        validationService.validateMessageRequest(messageRequest);

        return peopleRepository.findDirectMessages(messageRequest.fromPersonId(),
                        messageRequest.minTrustLevel(),
                        messageRequest.topics()).stream()
                .collect(Collectors.groupingBy(m -> m.get("idFrom"),
                        Collectors.mapping(m -> m.get("idTo"), Collectors.toList())));
    }

    public List<String> shortestPath(MessageRequestDto pathRequest) {
        validationService.validateMessageRequest(pathRequest);
        return peopleRepository.findShortestPath(pathRequest.fromPersonId(),
                        pathRequest.minTrustLevel(),
                        pathRequest.topics()).stream()
                .skip(1)
                .map(PersonNode::getId)
                .collect(Collectors.toList());
    }
}
