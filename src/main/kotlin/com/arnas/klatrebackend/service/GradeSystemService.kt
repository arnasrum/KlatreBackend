package com.arnas.klatrebackend.service

import com.arnas.klatrebackend.dataclass.GradeToCreate
import com.arnas.klatrebackend.dataclass.ServiceResult
import com.arnas.klatrebackend.repository.GradingSystemRepository
import org.springframework.stereotype.Service
import kotlin.math.roundToInt

@Service
class GradeSystemService(
    private val gradeSystemRepository: GradingSystemRepository
) {
    data class GradeInfo(val name: String, val from: Int, val to: Int)
    fun makeGradingSystem(
        groupID: Long,
        referenceGradeSystemID: Long,
        gradeSystemName: String,
        grades: List<Map<String, String>>
    ): ServiceResult<String> {
        val referenceGrades = gradeSystemRepository.getGradesBySystemId(referenceGradeSystemID)
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
            gradeSystemRepository.makeNewGradingSystem(groupID, gradeSystemName, newGrades)
            return ServiceResult(success = true, message = "Grade system created successfully")
        } catch (e: Exception) {
            return ServiceResult(success = false, message = "Error creating grade system: ${e.message}", errorCode = "400")
        }
    }

}