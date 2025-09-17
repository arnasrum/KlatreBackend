package com.arnas.klatrebackend.service

import com.arnas.klatrebackend.dataclass.BoulderWithSend
import com.arnas.klatrebackend.dataclass.RouteSend
import com.arnas.klatrebackend.dataclass.ServiceResult
import com.arnas.klatrebackend.interfaces.services.RouteSendServiceInterface
import com.arnas.klatrebackend.repository.RouteSendRepository
import org.springframework.stereotype.Service

@Service
class RouteSendService(
    private val routeSendRepository: RouteSendRepository,
    private val boulderRepository: com.arnas.klatrebackend.repository.BoulderRepository,
    private val imageService: ImageService
): RouteSendServiceInterface
{
    override fun getUserBoulderSends(userID: Long, boulderIDs: List<Long>): ServiceResult<List<RouteSend>> {
        return try {
            ServiceResult(data = routeSendRepository.getBoulderSends(userID, boulderIDs), success = true)
        } catch (e: Exception) {
            ServiceResult(success = false, message = "Error getting boulder sends", data = null)
        }
    }

    override fun getBouldersWithSendsByPlace(userID: Long, placeID: Long): ServiceResult<List<BoulderWithSend>> {
        return try {
            val boulders = boulderRepository.getBouldersByPlace(placeID)
            val boulderIDs = boulders.map { it.id }
            if(boulderIDs.isEmpty()) return ServiceResult(success = true, data = emptyList(), message = "No boulders found in this place")
            val routeSends = routeSendRepository.getBoulderSends(userID, boulderIDs)
            val bouldersWithSends = boulders.map { boulder ->
                val send = routeSends.filter { boulder.id == it.boulderID }
                boulder.image = imageService.getImageMetadataByBoulder(boulder.id).data?.let { "http://localhost:8080${it.getUrl()}" }
                BoulderWithSend(
                    boulder = boulder,
                    routeSend = send.firstOrNull()
                )
            }
            ServiceResult(success = true, data = bouldersWithSends, message = "Boulders retrieved successfully")
        } catch (e: Exception) {
            e.printStackTrace()
            ServiceResult(success = false, message = "Error getting boulder sends", data = null)
        }
    }

    override fun addUserRouteSend(userID: Long, boulderID: Long, additionalProps: Map<String, String>) {
        routeSendRepository.insertRouteSend(userID, boulderID, additionalProps)
    }

}