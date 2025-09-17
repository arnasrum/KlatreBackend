package com.arnas.klatrebackend.dataclasses


data class GradeToCreate(
    val gradeString: String,
    val numericalValue: Int,
)

data class Grade(
    val id: Long,
    val gradeString: String,
    val numericalValue: Int,
)

data class GradingSystem (
    val id: Long,
    val name: String,
    val climbType: String,
    val isGlobal: Boolean = false,
    val grades: List<Grade>
)