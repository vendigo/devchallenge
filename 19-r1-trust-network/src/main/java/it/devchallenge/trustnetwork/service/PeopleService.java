package it.devchallenge.trustnetwork.service;

import it.devchallenge.trustnetwork.model.CreatePersonDto;
import it.devchallenge.trustnetwork.model.PersonNode;
import it.devchallenge.trustnetwork.model.TrustedConnection;
import it.devchallenge.trustnetwork.repository.PeopleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PeopleService {

    private final PeopleRepository peopleRepository;
    private final ValidationService validationService;

    @Transactional
    public void createPerson(CreatePersonDto personDto) {
        validationService.validateCreatePersonRequest(personDto);
        PersonNode personNode = new PersonNode(personDto.id(), personDto.topics());
        peopleRepository.save(personNode);
    }

    @Transactional
    public void createAndUpdateConnections(String personId, Map<String, Integer> idToTrustLevel) {
        ValidationService.validateRequest(idToTrustLevel);
        PersonNode person = peopleRepository.findById(personId)
                .orElseThrow(ValidationService.PERSON_NOT_FOUND_ERROR);

        updateExistent(idToTrustLevel, person);
        createNew(idToTrustLevel, person);
    }

    private void createNew(Map<String, Integer> idToTrustLevel, PersonNode person) {
        Set<String> existentConnections = person.getConnections().stream()
                .map(TrustedConnection::getPerson)
                .map(PersonNode::getId)
                .collect(Collectors.toSet());
        List<TrustedConnection> newConnections = idToTrustLevel.entrySet().stream()
                .filter(entry -> !existentConnections.contains(entry.getKey()))
                .map(this::createConnection)
                .toList();
        person.setConnections(newConnections);
        peopleRepository.save(person);
    }

    private void updateExistent(Map<String, Integer> idToTrustLevel, PersonNode person) {
        person.getConnections().forEach(connection -> {
            Integer newValue = idToTrustLevel.get(connection.getPerson().getId());
            if (newValue != null) {
                connection.setTrustLevel(newValue);
            }
        });
        peopleRepository.save(person);
    }

    private TrustedConnection createConnection(Map.Entry<String, Integer> entry) {
        PersonNode personTo = peopleRepository.findById(entry.getKey()).orElseThrow(ValidationService.PERSON_NOT_FOUND_ERROR);
        return new TrustedConnection(entry.getValue(), personTo);
    }
}
