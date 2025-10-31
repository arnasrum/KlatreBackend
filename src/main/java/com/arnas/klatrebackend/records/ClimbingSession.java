package com.arnas.klatrebackend.records;

public record ClimbingSession(
        long id,
        long userId,
        long groupId,
        long placeId,
        long timestamp,
        boolean active,
        String name,
        RouteAttempt[] attempts
) { }
