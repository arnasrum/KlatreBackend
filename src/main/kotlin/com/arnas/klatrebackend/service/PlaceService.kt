package com.arnas.klatrebackend.service

import com.arnas.klatrebackend.dataclass.Place
import com.arnas.klatrebackend.dataclass.ServiceResult
import com.arnas.klatrebackend.repository.PlaceRepository
import org.springframework.stereotype.Service

@Service
class PlaceService(
    private val placeRepository: PlaceRepository,
    private val groupService: GroupService,
) {

    open fun getPlacesByGroupId(groupID: Long, userID: Long): ServiceResult<List<Place>> {
        return try {
            groupService.getGroupUserRole(userID, groupID).data ?: return ServiceResult(success = false, message = "User is not a member of group", data = null)
            val places = placeRepository.getPlacesByGroupId(groupID)
            ServiceResult(success = true, data = places, message = "Places retrieved successfully")
        } catch (e: Exception) {
            ServiceResult(success = false, message = "Error retrieving places: ${e.message}", data = null)
        }
    }
}