package com.arnas.klatrebackend.services

import com.arnas.klatrebackend.dataclasses.GradingSystemWithGrades
import com.arnas.klatrebackend.dataclasses.Place
import com.arnas.klatrebackend.dataclasses.PlaceUpdateDTO
import com.arnas.klatrebackend.dataclasses.PlaceWithGrades
import com.arnas.klatrebackend.dataclasses.ServiceResult
import com.arnas.klatrebackend.interfaces.repositories.BoulderRepositoryInterface
import com.arnas.klatrebackend.interfaces.repositories.GradingSystemRepositoryInterface
import com.arnas.klatrebackend.interfaces.repositories.PlaceRepositoryInterface
import com.arnas.klatrebackend.interfaces.services.GroupServiceInterface
import com.arnas.klatrebackend.interfaces.services.PlaceServiceInterface
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.math.abs
import kotlin.math.min

@Service
class PlaceService(
    private val placeRepository: PlaceRepositoryInterface,
    private val groupService: GroupServiceInterface,
    private val gradingSystemRepository: GradingSystemRepositoryInterface,
    private val boulderRepository: BoulderRepositoryInterface,
): PlaceServiceInterface {


    fun getPlaceById(placeId: Long): Place? {
        return placeRepository.getPlaceById(placeId)
    }

    override fun getPlacesByGroupId(groupId: Long, userId: Long): ServiceResult<List<PlaceWithGrades>> {
        return try {
            groupService.getGroupUserRole(userId, groupId).data
                ?: return ServiceResult(success = false, message = "User is not a member of group", data = null)
            val places = placeRepository.getPlacesByGroupId(groupId)
            val test = places.map {
                PlaceWithGrades(it.id, it.name, it.description, it.groupID, GradingSystemWithGrades(it.gradingSystem,
                    gradingSystemRepository.getGradesBySystemId(it.gradingSystem)))
            }
            ServiceResult(success = true, message = "Places retrieved successfully", data = test)
        } catch (e: Exception) {
            ServiceResult(success = false, message = "Error retrieving places: ${e.message}", data = null)
        }
    }

    override fun updatePlace(userId: Long, placeUpdateDTO: PlaceUpdateDTO): ServiceResult<Unit> {
        val place = placeRepository.getPlaceById(placeUpdateDTO.placeId)
        place?: throw RuntimeException("Place not found")
        val rowAffected = placeRepository.updatePlace(placeUpdateDTO)
        if(rowAffected <= 0 && placeUpdateDTO.gradingSystem == null) {
            throw RuntimeException("Failed to update place")
        }
        placeUpdateDTO.gradingSystem?.let {
            updatePlaceGradingSystem(userId, placeUpdateDTO.placeId, it)
        }
        return ServiceResult(success = true, message = "Place updated successfully")
    }


    @Transactional
    override fun updatePlaceGradingSystem(userId: Long, placeId: Long, newGradingSystemId: Long): ServiceResult<Unit> {
        val place = placeRepository.getPlaceById(placeId)?: return ServiceResult(success = false, message = "Place not found", data = null)
        val boulders = boulderRepository.getBouldersByPlace(placeId, 0, 0, false)
        val oldGradingSystem = gradingSystemRepository.getGradesBySystemId(place.gradingSystem)
        val newGradingSystem = gradingSystemRepository.getGradesBySystemId(newGradingSystemId)
        val newGradeValues = newGradingSystem.map { it.numericalValue }

        val placeUpdateResult = placeRepository.updatePlace(placeId, gradingSystem = newGradingSystemId, name = null, description = null, groupId = null)
        if (placeUpdateResult <= 0) {
            throw RuntimeException("Failed to update place grading system")
        }

        for(boulder in boulders) {
            val oldGrade = oldGradingSystem.find { it.id == boulder.gradeId}
                ?: throw RuntimeException("Boulder grade not found for boulder ${boulder.id}")

            val newGradeNumericalValue = findClosestInt(oldGrade.numericalValue, newGradeValues)
                ?: throw RuntimeException("Could not find closest grade value")

            val newGrade = newGradingSystem.find { it.numericalValue == newGradeNumericalValue }
                ?: throw RuntimeException("New grade not found for numerical value $newGradeNumericalValue")

            val boulderUpdateResult = boulderRepository.updateBoulder(
                grade = newGrade.id,
                routeId = boulder.id,
                name = null,
                place = null,
                description = null,
                active = null,
                imageId = null
            )

            if (boulderUpdateResult <= 0) {
                throw RuntimeException("Failed to update boulder ${boulder.id} grade")
            }
        }
        return ServiceResult(success = true, message = "Grading system successfully changed")
    }

    private fun findClosestInt(definedInt: Int, numbers: List<Int>): Int? {
        if (numbers.isEmpty()) {
            return null
        }

        var closestNumber = numbers.first()
        var minDifference = abs(definedInt - closestNumber)

        for (number in numbers) {
            val currentDifference = abs(definedInt - number)
            if (currentDifference < minDifference) {
                minDifference = currentDifference
                closestNumber = number
            } else if (currentDifference == minDifference) {
                // Handle ties by taking the smaller number
                closestNumber = min(closestNumber, number)
            }
        }
        return closestNumber
    }
}