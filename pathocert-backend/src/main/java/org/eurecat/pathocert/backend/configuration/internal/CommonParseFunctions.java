package org.eurecat.pathocert.backend.configuration.internal;

import java.util.Objects;
import java.util.Optional;

/**
 * Contains a bunch of static functions that are used to parse.
 */
public class CommonParseFunctions {

    public static <T> Optional<T> identity(T t) {
        return Optional.of(t);
    }

    public static Optional<Boolean> parseBoolean(String value) {
        if (Objects.equals(value, "true")) {
            return Optional.of(true);
        } else if (Objects.equals(value, "false")) {
            return Optional.of(false);
        }
        return Optional.empty();

    }

    public static Optional<Integer> parseInt(String value) {
        try {
            return Optional.of(Integer.parseInt(value));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

}
