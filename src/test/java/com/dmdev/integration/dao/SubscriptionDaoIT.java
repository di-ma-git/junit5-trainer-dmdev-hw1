package com.dmdev.integration.dao;

import com.dmdev.dao.SubscriptionDao;
import com.dmdev.entity.Provider;
import com.dmdev.entity.Status;
import com.dmdev.entity.Subscription;
import com.dmdev.integration.IntegrationTestBase;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class SubscriptionDaoIT extends IntegrationTestBase {
    private final SubscriptionDao subscriptionDao = SubscriptionDao.getInstance();

    @Test
    void findAll() {
        var subscription1 = subscriptionDao.insert(getSubscription(5, "subscription1"));
        var subscription2 = subscriptionDao.insert(getSubscription(6, "subscription1"));
        var subscription3 = subscriptionDao.insert(getSubscription(7, "subscription1"));
        var subscription4 = subscriptionDao.insert(getSubscription(456, "subscription1"));

        var actualResult = subscriptionDao.findAll();

        assertThat(actualResult).hasSize(4);
        var subscriptionIds = actualResult.stream()
                .map(Subscription::getId)
                .toList();
        assertThat(subscriptionIds).contains(
                subscription1.getId(),
                subscription2.getId(),
                subscription3.getId(),
                subscription4.getId()
        );

    }

    @Test
    void findById() {
        var subscription = subscriptionDao.insert(getSubscription(10, "subscription1"));

        var actualResult = subscriptionDao.findById(subscription.getId());

        assertThat(actualResult).isPresent();
        assertThat(actualResult.get().getId()).isEqualTo(subscription.getId());
        assertThat(actualResult.get()).isEqualTo(subscription);

    }

    @Test
    void deleteExistingSubscription() {
        var subscription = subscriptionDao.insert(getSubscription(5, "subscription1"));

        var actualResult = subscriptionDao.delete(subscription.getId());

        assertThat(actualResult).isTrue();

    }

    @Test
    void deleteNotExistingSubscription() {
        subscriptionDao.insert(getSubscription(5, "subscription1"));

        var actualResult = subscriptionDao.delete(999);

        assertThat(actualResult).isFalse();

    }

    @Test
    void update() {
        var subscription = subscriptionDao.insert(getSubscription(5, "subscription1"));
        subscription.setExpirationDate(subscription.getExpirationDate()
                .plus(30, ChronoUnit.DAYS).truncatedTo(ChronoUnit.SECONDS));

        subscriptionDao.update(subscription);

        var updatedResult = subscriptionDao.findById(subscription.getId());
        assertThat(updatedResult.get()).isEqualTo(subscription);
        assertThat(updatedResult.get().getExpirationDate()).isEqualTo(subscription.getExpirationDate());

    }

    @Test
    void insert() {
        var subscription = getSubscription(30, "subscription1");

        var actualResult = subscriptionDao.insert(subscription);

        assertNotNull(actualResult.getId());

    }

    @Test
    void findByUserId() {
        var subscription1 = subscriptionDao.insert(getSubscription(10, "subscription1"));
        var subscription2 = subscriptionDao.insert(getSubscription(10, "subscription2"));
        var subscription3 = subscriptionDao.insert(getSubscription(10, "subscription3"));

        var actualResult = subscriptionDao.findByUserId(10);

        assertThat(actualResult).hasSize(3);

    }

    @Test
    void emptyListIfUserDoNotHaveSubscription() {
        subscriptionDao.insert(getSubscription(11, "subscription1"));

        var actualResult = subscriptionDao.findByUserId(10);

        assertThat(actualResult).isEmpty();
    }


    private Subscription getSubscription(int userId, String subscriptionName) {
        var clock = Clock.systemDefaultZone();
        var now = ZonedDateTime.now(clock);
        return Subscription.builder()
                .userId(userId)
                .name(subscriptionName)
                .provider(Provider.APPLE)
//                .expirationDate(Instant.now().plus(30, ChronoUnit.DAYS))
                .expirationDate(now.toInstant().plus(30, ChronoUnit.DAYS).truncatedTo(ChronoUnit.SECONDS))
                .status(Status.ACTIVE)
                .build();
    }

}