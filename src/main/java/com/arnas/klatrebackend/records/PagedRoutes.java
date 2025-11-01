package com.arnas.klatrebackend.records;

import com.arnas.klatrebackend.dataclasses.Route;

import java.util.List;

public record PagedRoutes(
    List<Route>routes,
    int page,
    int limit,
    int activeRoutesCount,
    int retiredRoutesCount,
    boolean hasMore
)  {}
