package com.arnas.klatrebackend.services

import com.arnas.klatrebackend.dataclasses.GradeToCreate
import com.arnas.klatrebackend.dataclasses.Role
import com.arnas.klatrebackend.dataclasses.ServiceResult
import com.arnas.klatrebackend.interfaces.services.GradingSystemServiceInterface
import com.arnas.klatrebackend.repositories.GradingSystemRepository
import org.springframework.stereotype.Service

@Service
class GradeSystemService(
    private val gradeSystemRepository: GradingSystemRepository,
    private val groupService: GroupService
): GradingSystemServiceInterface {

    override fun makeGradingSystem(
        groupId: Long,
        referenceGradeSystemId: Long,
        gradingSystemName: String,
        grades: List<Map<String, String>>
    ): ServiceResult<Long> {
        val referenceGrades = gradeSystemRepository.getGradesBySystemId(referenceGradeSystemId)
        try {
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
            val id = gradeSystemRepository.makeNewGradingSystem(groupId, gradingSystemName, newGrades)
            return ServiceResult(data = id, success = true, message = "Grade system created successfully")
        } catch (e: Exception) {
            return ServiceResult(success = false, message = "Error creating grade system: ${e.message}", errorCode = "400")
        }
    }

    override fun deleteGradeSystem(gradingSystemId: Long, groupId: Long, userId: Long): ServiceResult<Unit> {
        val userRole = groupService.getGroupUserRole(userId, groupId).data ?: return ServiceResult(success = false, errorCode = "401", message = "User is not a member of group")
        if(!(userRole == Role.OWNER.id || userRole == Role.ADMIN.id)) return ServiceResult(success = false, errorCode = "401", message = "Only group owners and admins can delete grade systems")
        val rowsAffected = gradeSystemRepository.deleteGradingSystem(gradingSystemId)
        if(rowsAffected == 0) return ServiceResult(success = false, errorCode = "404", message = "Grade system not found")
        return ServiceResult(success = true, message = "Grade system deleted successfully")
    }

}