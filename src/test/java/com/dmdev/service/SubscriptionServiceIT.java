package com.dmdev.service;

import com.dmdev.dao.SubscriptionDao;
import com.dmdev.dto.CreateSubscriptionDto;
import com.dmdev.entity.Provider;
import com.dmdev.entity.Status;
import com.dmdev.entity.Subscription;
import com.dmdev.exception.SubscriptionException;
import com.dmdev.integration.IntegrationTestBase;
import com.dmdev.mapper.CreateSubscriptionMapper;
import com.dmdev.validator.CreateSubscriptionValidator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SubscriptionServiceIT extends IntegrationTestBase {
    private SubscriptionDao subscriptionDao;
    private SubscriptionService subscriptionService;
    private Clock clock;

    @BeforeEach
    void init() {
        subscriptionDao = SubscriptionDao.getInstance();

        clock = Clock.systemDefaultZone();
        subscriptionService = new SubscriptionService(
                subscriptionDao,
                CreateSubscriptionMapper.getInstance(),
                CreateSubscriptionValidator.getInstance(),
                clock
        );
    }

    @Test
    void upsert() {
        var subscriptionDto = getSubscriptionDto();

        var actualResult = subscriptionService.upsert(subscriptionDto);

        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getId()).isNotNull();
//        assertThat(actualResult).isEqualTo(subscription);
    }

    @Test
    void cancel() {
        var subscriptionDto = getSubscriptionDto();
        var subscription = subscriptionService.upsert(subscriptionDto);
        assertThat(subscription.getStatus()).isEqualTo(Status.ACTIVE);

        subscriptionService.cancel(subscription.getId());


        var actualResult = subscriptionDao.findById(subscription.getId());
        assertThat(actualResult.get().getStatus()).isEqualTo(Status.CANCELED);
    }

    @Test
    void cancelShouldThrowExceptionIfStatusIsCanceled() {
        var subscriptionDto = getSubscriptionDto();
        var subscription = subscriptionService.upsert(subscriptionDto);
        subscription.setStatus(Status.CANCELED);
        subscriptionDao.update(subscription);

        assertThrows(SubscriptionException.class, () -> subscriptionService.cancel(subscription.getId()));
    }

    @Test
    void expireShouldThrowExceptionIfStatusIsExpired() {
        var subscriptionDto = getSubscriptionDto();
        var subscription = subscriptionService.upsert(subscriptionDto);
        subscription.setStatus(Status.EXPIRED);
        subscriptionDao.update(subscription);

        assertThrows(SubscriptionException.class, () -> subscriptionService.expire(subscription.getId()));
    }

    @Test
    void expire() {
        var subscriptionDto = getSubscriptionDto();
        var subscription = subscriptionService.upsert(subscriptionDto);

        subscriptionService.expire(subscription.getId());

        var actualResult = subscriptionDao.findById(subscription.getId());
        assertThat(actualResult.get().getStatus()).isEqualTo(Status.EXPIRED);
        assertThat(actualResult.get().getExpirationDate()).isBefore(Instant.now(clock));
    }

    private CreateSubscriptionDto getSubscriptionDto() {
        var now = ZonedDateTime.now(clock);
        return CreateSubscriptionDto.builder()
                .userId(666)
                .name("subscription1")
                .expirationDate(now.toInstant().plus(30, ChronoUnit.DAYS).truncatedTo(ChronoUnit.SECONDS))
                .provider(Provider.GOOGLE.toString())
                .build();
    }
}
