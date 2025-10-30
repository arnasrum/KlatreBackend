package com.arnas.KlatreBackend.records;

import org.springframework.lang.NonNull;

public record RouteDTO(
        @NonNull String name,
        @NonNull Long gradeId,
        @NonNull Long placeId,
        boolean active,
        String image,
        String description
) {}
