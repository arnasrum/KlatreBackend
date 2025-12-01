package com.arnas.klatrebackend.features.climbingsessions;

public record RouteAttemptDTO (
    int attempts,
    boolean completed,
    long routeId,
    long timestamp,
    long session

) { }
