package com.arnas.klatrebackend.features.climbingsessions;

public record UpdateAttemptRequest(
    long id,
    int attempts,
    boolean completed,
    long timestamp
) { }
