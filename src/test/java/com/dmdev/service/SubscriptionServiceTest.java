package com.dmdev.service;

import com.dmdev.dao.SubscriptionDao;
import com.dmdev.dto.CreateSubscriptionDto;
import com.dmdev.entity.Provider;
import com.dmdev.entity.Status;
import com.dmdev.entity.Subscription;
import com.dmdev.exception.SubscriptionException;
import com.dmdev.mapper.CreateSubscriptionMapper;
import com.dmdev.validator.CreateSubscriptionValidator;
import com.dmdev.validator.ValidationResult;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith({
        MockitoExtension.class
})
class SubscriptionServiceTest {
    @Mock
    private CreateSubscriptionValidator subscriptionValidator;
    @Mock
    private CreateSubscriptionMapper subscriptionMapper;
    @Mock
    private SubscriptionDao subscriptionDao;
    @InjectMocks
    private SubscriptionService subscriptionService;

    @Test
    void upsertSubscriptionSuccessTest() {

        var subscription = Subscription.builder()
                .id(555)
                .userId(666)
                .name("subscription1")
                .provider(Provider.APPLE)
                .expirationDate(Instant.now().plus(30, ChronoUnit.DAYS))
                .status(Status.ACTIVE)
                .build();
        var subscriptionDto = CreateSubscriptionDto.builder()
                .userId(666)
                .name("subscription1")
                .provider("APPLE")
                .expirationDate(Instant.now().plus(30, ChronoUnit.DAYS))
                .build();
        doReturn(new ValidationResult()).when(subscriptionValidator).validate(subscriptionDto);
        doReturn(new ArrayList<Subscription>()).when(subscriptionDao).findByUserId(subscriptionDto.getUserId());
        doReturn(subscription).when(subscriptionMapper).map(subscriptionDto);

        var actualResult = subscriptionService.upsert(subscriptionDto);
// ЗАПУТАЛСЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯЯ
//        assertThat(actualResult).isEqualTo(subscription);
//        assertThat(actualResult).isEqualTo(subscriptionDto);
        verify(subscriptionDao).upsert(subscription);

    }

    @Test
    void upsertSubscriptionFailedTest() {

    }

    @Test
    void cancel() {
        var subscription = Subscription.builder()
                .id(555)
                .userId(666)
                .name("subscription1")
                .provider(Provider.APPLE)
                .expirationDate(Instant.now().plus(30, ChronoUnit.DAYS))
                .status(Status.ACTIVE)
                .build();
        doReturn(Optional.of(subscription)).when(subscriptionDao).findById(subscription.getId());

        subscriptionService.cancel(subscription.getId());

        assertThat(subscription.getStatus()).isEqualTo(Status.CANCELED);
        verify(subscriptionDao).findById(subscription.getId());
        verify(subscriptionDao).update(subscription);
    }
    @Test
    void cancelThrowExceptionIfSubscriptionIdDoesNotExist() {
        var subscription = Subscription.builder()
                .id(555)
                .userId(666)
                .name("subscription1")
                .provider(Provider.APPLE)
                .expirationDate(Instant.now().plus(30, ChronoUnit.DAYS))
                .status(Status.ACTIVE)
                .build();

        doThrow(IllegalArgumentException.class).when(subscriptionDao).findById(subscription.getId());

        assertThrows(IllegalArgumentException.class, () -> subscriptionService.cancel(subscription.getId()));
        Mockito.verify(subscriptionDao, times(0)).update(any());
    }


    @Test
    void expire() {
    }
}