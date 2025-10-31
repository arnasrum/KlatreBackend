package com.arnas.klatrebackend.records;

import org.springframework.lang.NonNull;

public record PlaceDTO(
        @NonNull String name,
        String description,
        long groupId,
        long gradingSystemId
) { }
