package com.arnas.klatrebackend.records;

import java.util.List;

public record ClimbingSession(
        long id,
        long userId,
        long groupId,
        long placeId,
        long timestamp,
        boolean active,
        String name,
        List<RouteAttempt> attempts
) { }
