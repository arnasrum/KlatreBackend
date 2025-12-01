package com.arnas.klatrebackend.features.climbingsessions;

public record RouteAttempt(
    long id,
    int attempts,
    boolean completed,
    long routeId,
    long timestamp,
    long session
) { }
