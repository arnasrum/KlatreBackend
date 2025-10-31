package com.arnas.klatrebackend.records;

public record Grade(
    Long id,
    String gradeString,
    int numericValue
) {}
