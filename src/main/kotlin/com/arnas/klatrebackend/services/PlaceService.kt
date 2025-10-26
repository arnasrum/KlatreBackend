package com.arnas.klatrebackend.services

import com.arnas.klatrebackend.annotation.RequireGroupAccess
import com.arnas.klatrebackend.dataclasses.GradingSystemWithGrades
import com.arnas.klatrebackend.dataclasses.Place
import com.arnas.klatrebackend.dataclasses.PlaceUpdateDTO
import com.arnas.klatrebackend.dataclasses.PlaceWithGrades
import com.arnas.klatrebackend.dataclasses.ServiceResult
import com.arnas.klatrebackend.interfaces.repositories.RouteRepositoryInterface
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
    private val gradingSystemRepository: GradingSystemRepositoryInterface,
    private val boulderRepository: RouteRepositoryInterface,
): PlaceServiceInterface {


    override fun getPlaceById(placeId: Long): Place? {
        return placeRepository.getPlaceById(placeId)
    }

    @RequireGroupAccess
    override fun getPlacesByGroupId(groupId: Long, userId: Long): List<PlaceWithGrades> {
        val places = placeRepository.getPlacesByGroupId(groupId)
        return places.map {
            PlaceWithGrades(
                it.id,
                it.name,
                it.description,
                it.groupID,
                GradingSystemWithGrades(it.gradingSystem, gradingSystemRepository.getGradesBySystemId(it.gradingSystem))
            )
        }
    }

    @Transactional
    override fun updatePlace(userId: Long, placeUpdateDTO: PlaceUpdateDTO) {
        val place = placeRepository.getPlaceById(placeUpdateDTO.placeId)
        place?: throw RuntimeException("Place not found")
        val rowAffected = placeRepository.updatePlace(placeUpdateDTO)
        if(rowAffected <= 0 && placeUpdateDTO.gradingSystem == null) {
            throw RuntimeException("Failed to update place")
        }
        placeUpdateDTO.gradingSystem?.let {
            updatePlaceGradingSystem(userId, placeUpdateDTO.placeId, it)
        }
    }


    @Transactional
    override fun updatePlaceGradingSystem(userId: Long, placeId: Long, newGradingSystemId: Long) {
        val place = placeRepository.getPlaceById(placeId)?: throw RuntimeException("Place not found")
        val boulders = boulderRepository.getRoutesByPlace(placeId, 0, 0, false)
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

            val boulderUpdateResult = boulderRepository.updateRoute(
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