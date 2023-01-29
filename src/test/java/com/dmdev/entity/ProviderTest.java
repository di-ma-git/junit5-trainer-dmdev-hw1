package com.dmdev.entity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import static com.dmdev.entity.Provider.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

class ProviderTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "Google", "google", "GoOgLe", "GOOGLE",
            "Apple", "apple", "ApPlE", "APPLE"
    })
    void findByName(String name) {
        var actualResult = Provider.findByName(name);

        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isInstanceOf(Provider.class);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {
            "Amazon", "12345", "пятерочка"
    })
    void shouldThrowExceptionIfNameIsInvalid(String name) {
        assertThrows(NoSuchElementException.class, () -> Provider.findByName(name));
    }

    @ParameterizedTest
    @MethodSource("getArgumentsForProviderTest")
    void findByNameExample2(String name, Provider expectedProvider) {
        var actualResult = Provider.findByName(name);

        assertThat(actualResult).isEqualTo(expectedProvider);
    }

    static Stream<Arguments> getArgumentsForProviderTest() {
        return Stream.of(
                Arguments.of("Google", GOOGLE),
                Arguments.of("google", GOOGLE),
                Arguments.of("GoOgLe", GOOGLE),
                Arguments.of("GOOGLE", GOOGLE),
                Arguments.of("Apple", APPLE),
                Arguments.of("apple", APPLE),
                Arguments.of("ApPlE", APPLE),
                Arguments.of("APPLE", APPLE)
        );
    }
}