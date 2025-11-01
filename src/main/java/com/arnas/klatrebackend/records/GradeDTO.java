package com.arnas.klatrebackend.records;

import org.springframework.lang.NonNull;

public record GradeDTO(
        @NonNull String gradeString,
        int numericValue
) { }
