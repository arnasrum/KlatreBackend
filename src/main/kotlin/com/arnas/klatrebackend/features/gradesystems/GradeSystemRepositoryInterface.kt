package com.arnas.klatrebackend.features.gradesystems

interface GradeSystemRepositoryInterface {
    fun getGradesBySystemId(systemId: Long): List<Grade>
    fun makeNewGradingSystem(groupId: Long, gradingSystemName: String, grades: List<GradeToCreate>): Long
    fun getGradingSystemsInGroup(groupID: Long): List<GradingSystem>
    fun deleteGradingSystem(gradingSystemId: Long): Int
}