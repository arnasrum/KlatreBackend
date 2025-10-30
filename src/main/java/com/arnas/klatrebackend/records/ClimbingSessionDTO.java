package com.arnas.KlatreBackend.records;

public record ClimbingSessionDTO(
        long userId,
        long groupId,
        long placeId,
        long timestamp,
        String name,
        RouteAttempt[] attempts
) { }
