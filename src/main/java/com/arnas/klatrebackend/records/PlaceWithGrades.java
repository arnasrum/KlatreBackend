package com.arnas.klatrebackend.records;

import org.springframework.lang.NonNull;

public record PlaceWithGrades(
    long id,
    @NonNull String name,
    String description,
    long groupID,
    GradingSystemWithGrades gradingSystem
) { }
