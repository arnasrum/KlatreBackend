package com.arnas.klatrebackend.records;

public record RouteAttempt(
    long id,
    int attempts,
    boolean completed,
    long routeId,
    long timestamp,
    long session
) { }
