package com.arnas.klatrebackend.features.climbingsessions;

public record RouteAttemptDisplay(
    long id,
    int attempts,
    boolean completed,
    String routeName,
    long timestamp,
    String gradeName
) { }
