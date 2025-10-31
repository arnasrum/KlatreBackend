package com.arnas.klatrebackend.records;

import org.springframework.lang.NonNull;

public record Place(
        Long id,
        @NonNull String name,
        String description,
        long groupId,
        long gradingSystemId
) { }
