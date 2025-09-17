package com.arnas.klatrebackend.services

import com.arnas.klatrebackend.dataclasses.Place
import com.arnas.klatrebackend.dataclasses.ServiceResult
import com.arnas.klatrebackend.interfaces.services.PlaceServiceInterface
import com.arnas.klatrebackend.repositories.PlaceRepository
import org.springframework.stereotype.Service

@Service
class PlaceService(
    private val placeRepository: PlaceRepository,
    private val groupService: GroupService,
): PlaceServiceInterface {

    override fun getPlacesByGroupId(groupId: Long, userId: Long): ServiceResult<List<Place>> {
        return try {
            groupService.getGroupUserRole(userId, groupId).data ?: return ServiceResult(success = false, message = "User is not a member of group", data = null)
            val places = placeRepository.getPlacesByGroupId(groupId)
            ServiceResult(success = true, data = places, message = "Places retrieved successfully")
        } catch (e: Exception) {
            ServiceResult(success = false, message = "Error retrieving places: ${e.message}", data = null)
        }
    }
}