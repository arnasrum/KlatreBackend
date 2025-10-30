package com.arnas.KlatreBackend.records;

public record ClimbingSession(
        long id,
        long userId,
        long groupId,
        long placeId,
        long timestamp,
        String name,
        RouteAttempt[] attempts
) { }
