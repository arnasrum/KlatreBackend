package com.arnas.klatrebackend.interfaces.services

interface GradeSystemServiceInterface {
    fun makeGradingSystem(groupId: Long, userId: Long, referenceGradeSystemId: Long, gradingSystemName: String, grades: List<Map<String, String>>): Long
    fun deleteGradeSystem(gradingSystemId: Long, groupId: Long, userId: Long)
}