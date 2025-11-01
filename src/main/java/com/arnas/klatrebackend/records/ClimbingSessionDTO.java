package com.arnas.klatrebackend.records;

import java.util.List;

public record ClimbingSessionDTO(
        long userId,
        long groupId,
        long placeId,
        long timestamp,
        String name,
        List<RouteAttempt> attempts
) { }
