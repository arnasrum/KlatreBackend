package com.arnas.klatrebackend.records;

import com.arnas.klatrebackend.records.RouteAttempt;

public record ClimbingSessionDTO(
        long userId,
        long groupId,
        long placeId,
        long timestamp,
        String name,
        RouteAttempt[] attempts
) { }
