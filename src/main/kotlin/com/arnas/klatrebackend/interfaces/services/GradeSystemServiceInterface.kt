package com.arnas.klatrebackend.interfaces.services

import com.arnas.klatrebackend.dataclass.ServiceResult

interface GradeSystemServiceInterface {
    fun makeGradingSystem(groupId: Long, referenceGradeSystemId: Long, gradingSystemName: String, grades: List<Map<String, String>>): ServiceResult<Long>
}