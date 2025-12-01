package com.arnas.klatrebackend.features.stats;

public record UserGroupSessionStats(
    int year,
    int month,
    int day,
    int routesTried,
    int totalTries,
    int totalCompleted,
    long groupId,
    long userId
) {
}
