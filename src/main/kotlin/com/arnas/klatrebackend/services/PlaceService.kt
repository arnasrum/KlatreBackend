package com.arnas.klatrebackend.services

import com.arnas.klatrebackend.annotation.RequireGroupAccess
import com.arnas.klatrebackend.dataclasses.GradingSystemWithGrades
import com.arnas.klatrebackend.dataclasses.Place
import com.arnas.klatrebackend.dataclasses.PlaceUpdateDTO
import com.arnas.klatrebackend.dataclasses.PlaceWithGrades
import com.arnas.klatrebackend.dataclasses.Route
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
    private val routeService: RouteRepositoryInterface,
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
                it.groupId,
                GradingSystemWithGrades(it.gradingSystemId,
                    gradingSystemRepository.getGradesBySystemId(it.gradingSystemId))
            )
        }
    }

    @Transactional
    override fun updatePlace(userId: Long, placeUpdateDTO: PlaceUpdateDTO) {
        val place = placeRepository.getPlaceById(placeUpdateDTO.placeId)
        place?: throw RuntimeException("Place not found")
        val newPlace = Place(
            id = placeUpdateDTO.placeId,
            name = placeUpdateDTO.name ?: place.name,
            description = placeUpdateDTO.description ?: place.description,
            groupId = placeUpdateDTO.groupId ?: place.groupId,
            gradingSystemId = updatePlaceGradingSystem(
                place.id,
                place.gradingSystemId,
                placeUpdateDTO.gradingSystemId
            )
        )
        val rowAffected = placeRepository.updatePlace(newPlace)
        if(rowAffected <= 0 && placeUpdateDTO.gradingSystemId == null) {
            throw RuntimeException("Failed to update place")
        }
    }


    override fun updatePlaceGradingSystem(placeId: Long, oldGradingSystemId: Long, newGradingSystemId: Long?): Long {
        if(newGradingSystemId == null || oldGradingSystemId == newGradingSystemId) {return oldGradingSystemId}
        val routes = routeService.getRoutesByPlaceId(placeId)
        val oldGradingSystem = gradingSystemRepository.getGradesBySystemId(oldGradingSystemId)
        val newGradingSystem = gradingSystemRepository.getGradesBySystemId(newGradingSystemId)
        val newGradeValues = newGradingSystem.map { it.numericalValue }

        for(route in routes) {
            val oldGrade = oldGradingSystem.find { it.id == route.gradeId}
                ?: throw RuntimeException("Boulder grade not found for boulder ${route.id}, ${route.gradeId}, ${oldGradingSystem}}")

            val newGradeNumericalValue = findClosestInt(oldGrade.numericalValue, newGradeValues)
                ?: throw RuntimeException("Could not find closest grade value")

            val newGrade = newGradingSystem.find { it.numericalValue == newGradeNumericalValue }
                ?: throw RuntimeException("New grade not found for numerical value $newGradeNumericalValue")

            val routeUpdateResult = routeService.updateRoute(
                route.copy(gradeId = newGrade.id)
            )

            if (routeUpdateResult <= 0) {
                throw RuntimeException("Failed to update boulder ${route.id} grade")
            }
        }
        return newGradingSystemId
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