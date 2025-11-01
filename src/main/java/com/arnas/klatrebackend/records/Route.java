package com.arnas.klatrebackend.records;

import org.springframework.lang.NonNull;

public record Route(
        @NonNull Long id,
        @NonNull String name,
        @NonNull Long gradeId,
        @NonNull Long placeId,
        boolean active,
        String description,
        String image
) {}

