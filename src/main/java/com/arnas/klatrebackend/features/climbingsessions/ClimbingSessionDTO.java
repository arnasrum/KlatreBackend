package com.arnas.klatrebackend.features.climbingsessions;

public record ClimbingSessionDTO(
    long userId,
    long groupId,
    long placeId,
    long timestamp
) { }
