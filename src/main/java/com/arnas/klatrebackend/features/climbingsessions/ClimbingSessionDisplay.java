package com.arnas.klatrebackend.features.climbingsessions;

import java.util.List;

public record ClimbingSessionDisplay(
    long id,
    long groupId,
    long userId,
    long placeId,
    long timestamp,
    boolean active,
    List<RouteAttemptDisplay> routeAttempts
) { }
