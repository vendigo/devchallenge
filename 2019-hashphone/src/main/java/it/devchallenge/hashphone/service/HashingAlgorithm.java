package it.devchallenge.hashphone.service;

import java.util.function.Supplier;
import java.util.stream.Stream;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import it.devchallenge.hashphone.validation.ValidationException;
import lombok.AllArgsConstructor;

@SuppressWarnings("UnstableApiUsage")
@AllArgsConstructor
public enum HashingAlgorithm {
    SHA1("SHA-1", Hashing::sha1),
    SHA2("SHA-2", Hashing::sha256),
    SHA3("SHA-3", Hashing::sha384);

    String name;
    Supplier<HashFunction> hashFunctionSupplier;

    public static HashFunction getByAlgoName(String algoName) {
        return Stream.of(values())
            .filter(hashingAlgorithm -> hashingAlgorithm.name.equalsIgnoreCase(algoName))
            .findFirst()
            .map(hashingAlgorithm -> hashingAlgorithm.hashFunctionSupplier.get())
            .orElseThrow(() -> new ValidationException("Hashing algorithm " + algoName + " is not supported"));
    }
}
