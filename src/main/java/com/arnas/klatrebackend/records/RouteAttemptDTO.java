package com.arnas.KlatreBackend.records;

public record RouteAttemptDTO(
    int attempts,
    boolean completed,
    long routeId,
    long timestamp,
    long session
) { }
