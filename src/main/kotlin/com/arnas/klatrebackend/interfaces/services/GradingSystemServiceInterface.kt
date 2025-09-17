package com.arnas.klatrebackend.interfaces.services

import com.arnas.klatrebackend.dataclasses.ServiceResult

interface GradingSystemServiceInterface {
    fun makeGradingSystem(groupId: Long, referenceGradeSystemId: Long, gradingSystemName: String, grades: List<Map<String, String>>): ServiceResult<Long>
    fun deleteGradeSystem(gradingSystemId: Long, groupId: Long, userId: Long): ServiceResult<Unit>
}