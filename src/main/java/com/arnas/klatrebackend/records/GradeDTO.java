package com.arnas.KlatreBackend.records;

import org.springframework.lang.NonNull;

public record GradeDTO(
        @NonNull String gradeString,
        int numericValue
) { }
