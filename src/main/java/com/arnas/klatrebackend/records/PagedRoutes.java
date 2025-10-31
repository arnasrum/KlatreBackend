package com.arnas.klatrebackend.records;

public record PagedRoutes(
    Route[] routes,
    int page,
    int limit,
    int activeRoutesCount,
    int retiredRoutesCount,
    boolean hasMore
)  {}
