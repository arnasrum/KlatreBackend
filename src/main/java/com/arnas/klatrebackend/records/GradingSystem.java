package com.arnas.KlatreBackend.records;

import org.springframework.lang.NonNull;

public record GradingSystem(
    long id,
    @NonNull String name,
    @NonNull String description,
    boolean isGlobal,
    Grade[] grades

) { }
