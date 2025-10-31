package com.arnas.klatrebackend.records;

public record RouteAttemptDTO(
    int attempts,
    boolean completed,
    long routeId,
    long timestamp,
    long session
) { }
