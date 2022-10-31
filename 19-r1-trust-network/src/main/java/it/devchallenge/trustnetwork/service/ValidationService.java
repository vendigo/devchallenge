package it.devchallenge.trustnetwork.service;

import it.devchallenge.trustnetwork.exception.ValidationException;
import it.devchallenge.trustnetwork.model.CreatePersonDto;
import it.devchallenge.trustnetwork.model.MessageRequestDto;
import it.devchallenge.trustnetwork.repository.PeopleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class ValidationService {
    public static final String INVALID_TRUST_LEVEL = "Trust Level should be from 0 to 10";
    public static final String ID_NOT_NULL = "Person id should be not null";
    public static final String PERSON_ALREADY_EXISTS = "Person with provided id already exists";
    public static final String PERSON_NOT_FOUND = "Provided person not found";
    public static final String ALL_FIELDS_REQUIRED = "All fields are required";
    public static final Supplier<ValidationException> PERSON_NOT_FOUND_ERROR = () -> new ValidationException(PERSON_NOT_FOUND);

    private final PeopleRepository peopleRepository;

    public void validateCreatePersonRequest(CreatePersonDto personDto) {
        String personId = personDto.id();
        if (personId == null) {
            throw new ValidationException(ID_NOT_NULL);
        }

        if (peopleRepository.existsById(personId)) {
            throw new ValidationException(PERSON_ALREADY_EXISTS);
        }
    }

    public void validateMessageRequest(MessageRequestDto request) {
        boolean nullField = Stream.of(request.text(), request.fromPersonId(), request.topics(), request.minTrustLevel())
                .anyMatch(Objects::isNull);
        if (nullField) {
            throw new ValidationException(ALL_FIELDS_REQUIRED);
        }
        validateTrustLevel(request.minTrustLevel());
        validatePersonExists(request.fromPersonId());
    }

    private void validatePersonExists(String personId) {
        if (!peopleRepository.existsById(personId)) {
            throw new ValidationException(PERSON_NOT_FOUND);
        }
    }

    public static void validateRequest(Map<String, Integer> idToTrustLevel) {
        idToTrustLevel.values()
                .forEach(ValidationService::validateTrustLevel);
    }

    private static void validateTrustLevel(Integer trustLevel) {
        if (trustLevel < 0 || trustLevel > 10) {
            throw new ValidationException(INVALID_TRUST_LEVEL);
        }
    }
}
