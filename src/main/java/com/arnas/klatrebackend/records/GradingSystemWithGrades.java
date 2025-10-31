package com.arnas.klatrebackend.records;

public record GradingSystemWithGrades(
        long id,
        Grade[] grades
) { }
