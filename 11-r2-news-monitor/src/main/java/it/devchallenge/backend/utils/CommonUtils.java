package it.devchallenge.backend.utils;

import java.util.function.BinaryOperator;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommonUtils {

    /**
     * Binary operator which always returns first argument.
     * Is used for resolving conflicts in {@link java.util.stream.Collectors#toMap}
     */
    public static <T> BinaryOperator<T> pickFirstStrategy() {
        return (l, r) -> l;
    }
}
