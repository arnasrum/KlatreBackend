package com.arnas.klatrebackend.services

import com.arnas.klatrebackend.annotation.RequireGroupAccess
import com.arnas.klatrebackend.dataclasses.GradeToCreate
import com.arnas.klatrebackend.dataclasses.Role
import com.arnas.klatrebackend.dataclasses.ServiceResult
import com.arnas.klatrebackend.interfaces.repositories.GradingSystemRepositoryInterface
import com.arnas.klatrebackend.interfaces.services.GradeSystemServiceInterface
import com.arnas.klatrebackend.interfaces.services.GroupServiceInterface
import org.springframework.stereotype.Service

@Service
class GradeSystemService(
    private val gradeSystemRepository: GradingSystemRepositoryInterface,
    private val groupService: GroupServiceInterface
): GradeSystemServiceInterface {

    @RequireGroupAccess(minRole = Role.ADMIN)
    override fun makeGradingSystem(
        groupId: Long,
        userId: Long,
        referenceGradeSystemId: Long,
        gradingSystemName: String,
        grades: List<Map<String, String>>
    ): Long {
        val referenceGrades = gradeSystemRepository.getGradesBySystemId(referenceGradeSystemId)
        val newGrades = grades.map {
            if(it["from"] == null || it["to"] == null || it["name"] == null)
                throw Exception("Range is incorrectly defined")
            val from = it["from"]!!.toInt()
            val to = it["to"]!!.toInt()
            val numericalValue = referenceGrades.slice(from..to)
                .map { refGrade -> refGrade.numericalValue}
                .reduce { acc, numericalValue -> acc + numericalValue }
            return@map GradeToCreate(it["name"]!!, (numericalValue / (to - from + 1)))
        }
        return gradeSystemRepository.makeNewGradingSystem(groupId, gradingSystemName, newGrades)
    }

    @RequireGroupAccess(minRole = Role.ADMIN)
    override fun deleteGradeSystem(gradingSystemId: Long, groupId: Long, userId: Long) {
        val rowsAffected = gradeSystemRepository.deleteGradingSystem(gradingSystemId)
        if(rowsAffected == 0) throw Exception("Grade system with ID $gradingSystemId not found, cannot delete it.")
    }
}