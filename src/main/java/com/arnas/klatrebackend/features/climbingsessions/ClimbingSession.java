package com.arnas.klatrebackend.features.climbingsessions;

import java.util.List;

public record ClimbingSession(
    long id,
    long userId,
    long placeId,
    long groupId,
    long timestamp,
    boolean active,
    List<RouteAttempt> routeAttempts
) { }