package com.arnas.KlatreBackend.records;

public record GradingSystemWithGrades(
        long id,
        Grade[] grades
) { }
