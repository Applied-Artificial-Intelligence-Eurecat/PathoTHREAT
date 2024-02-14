package org.eurecat.pathocert.backend.configuration.internal;

import org.eurecat.pathocert.backend.configuration.internal.exceptions.RequiredPropertyException;
import org.eurecat.pathocert.backend.configuration.internal.exceptions.UnparsableProperty;

import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;

/**
 * Contains utils to read the properties.
 */
public class ReaderConfiguration {

    private final Properties properties;

    public ReaderConfiguration(String configs) {
        properties = ReaderUtils.read(configs);
    }

    public <T> T getRequiredProperty(String key, Function<String, Optional<T>> parse) throws RequiredPropertyException, UnparsableProperty {
        return getOptionalProperty(key, parse)
                .orElseThrow(() ->
                        new RequiredPropertyException(
                                "Property with key \"" + key + "\" is required but couldn't be found.")
                );

    }

    public <T> Optional<T> getOptionalProperty(String key, Function<String, Optional<T>> parse) throws UnparsableProperty {
        var val = properties.getProperty(key);
        return val == null ? Optional.empty() :
                Optional.of(parse.apply(val)
                        .orElseThrow(() -> new UnparsableProperty(
                                "Couldn't parse key '" + key + "' with value '" + val + "'.")));

    }

}
