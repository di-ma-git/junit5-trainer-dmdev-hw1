package com.dmdev.mapper;

import com.dmdev.dto.CreateSubscriptionDto;
import com.dmdev.entity.Provider;
import com.dmdev.entity.Status;
import com.dmdev.entity.Subscription;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class CreateSubscriptionMapperTest {
    private final CreateSubscriptionMapper subscriptionMapper = CreateSubscriptionMapper.getInstance();

    @Test
    void map() {
        var expirationDate = Instant.now().plus(30, ChronoUnit.DAYS);
        var subscriptionDto = CreateSubscriptionDto.builder()
                .userId(5)
                .name("subscription1")
                .provider("APPLE")
                .expirationDate(expirationDate)
                .build();


        var actualResult = subscriptionMapper.map(subscriptionDto);
        var expectedResult = Subscription.builder()
                .id(null)
                .userId(5)
                .name("subscription1")
                .provider(Provider.APPLE)
                .expirationDate(expirationDate)
                .status(Status.ACTIVE)
                .build();


        assertThat(actualResult).isInstanceOf(Subscription.class);
        assertThat(actualResult).isEqualTo(expectedResult);
    }
}