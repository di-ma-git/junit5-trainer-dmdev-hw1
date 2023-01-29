package com.dmdev.validator;

import com.dmdev.dto.CreateSubscriptionDto;
import com.dmdev.entity.Provider;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class CreateSubscriptionValidatorTest {
    private final CreateSubscriptionValidator subscriptionValidator = CreateSubscriptionValidator.getInstance();

    @Test
    void validate() {
        var subscriptionDto = CreateSubscriptionDto.builder()
                .userId(5)
                .name("subscription1")
                .provider("APPLE")
                .expirationDate(Instant.now().plus(30, ChronoUnit.DAYS))
                .build();

        var actualResult = subscriptionValidator.validate(subscriptionDto);

        assertThat(actualResult.getErrors()).isEmpty();
        assertFalse(actualResult.hasErrors());

    }

    @Test
    void shouldHaveErrorIfUserIdIsInvalid() {
        var subscriptionDto = CreateSubscriptionDto.builder()
                .userId(null)
                .name("subscription1")
                .provider("APPLE")
                .expirationDate(Instant.now().plus(30, ChronoUnit.DAYS))
                .build();

        var actualResult = subscriptionValidator.validate(subscriptionDto);

        assertThat(actualResult.getErrors()).hasSize(1);
        assertThat(actualResult.getErrors().get(0).getCode()).isEqualTo(100);
        assertThat(actualResult.getErrors().get(0).getMessage()).isEqualTo("userId is invalid");
    }

    @Test
    void shouldHaveErrorIfNameIsInvalid() {
        var subscriptionDto = CreateSubscriptionDto.builder()
                .userId(5)
                .name(null)
                .provider("APPLE")
                .expirationDate(Instant.now().plus(30, ChronoUnit.DAYS))
                .build();

        var actualResult = subscriptionValidator.validate(subscriptionDto);

        assertThat(actualResult.getErrors()).hasSize(1);
        assertThat(actualResult.getErrors().get(0).getCode()).isEqualTo(101);
        assertThat(actualResult.getErrors().get(0).getMessage()).isEqualTo("name is invalid");

    }

    @Test
    void shouldHaveErrorIfProviderIsInvalid() {
        var subscriptionDto = CreateSubscriptionDto.builder()
                .userId(5)
                .name("subscription1")
                .provider("AMAZON")
                .expirationDate(Instant.now().plus(30, ChronoUnit.DAYS))
                .build();

        var actualResult = subscriptionValidator.validate(subscriptionDto);

        assertThat(actualResult.getErrors()).hasSize(1);
        assertThat(actualResult.getErrors().get(0).getCode()).isEqualTo(102);
        assertThat(actualResult.getErrors().get(0).getMessage()).isEqualTo("provider is invalid");
    }

    @Test
    void shouldHaveErrorIfExpirationDateIsInvalid() {
        var subscriptionDto = CreateSubscriptionDto.builder()
                .userId(5)
                .name("subscription1")
                .provider("APPLE")
                .expirationDate(null)
                .build();

        var actualResult = subscriptionValidator.validate(subscriptionDto);

        assertThat(actualResult.getErrors()).hasSize(1);
        assertThat(actualResult.getErrors().get(0).getCode()).isEqualTo(103);
        assertThat(actualResult.getErrors().get(0).getMessage()).isEqualTo("expirationDate is invalid");
    }

    @Test
    void shouldHaveErrorIfProviderAndUserIdIsInvalid() {
        var subscriptionDto = CreateSubscriptionDto.builder()
                .userId(null)
                .name("subscription1")
                .provider("AMAZON")
                .expirationDate(Instant.now().plus(30, ChronoUnit.DAYS))
                .build();

        var actualResult = subscriptionValidator.validate(subscriptionDto);

        assertThat(actualResult.getErrors()).hasSize(2);
        var errors = actualResult.getErrors().stream()
                .map(Error::getCode)
                .toList();

        assertThat(errors).contains(100, 102);
    }

}