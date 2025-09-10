package com.arnas.klatrebackend.dataclass

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