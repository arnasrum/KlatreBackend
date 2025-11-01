package com.arnas.klatrebackend.records;

import com.arnas.klatrebackend.dataclasses.GradingSystemWithGrades;
import org.springframework.lang.NonNull;

public record PlaceWithGrades(
    long id,
    @NonNull String name,
    String description,
    long groupID,
    GradingSystemWithGrades gradingSystem
) { }
