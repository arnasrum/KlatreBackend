package com.arnas.klatrebackend.services

import com.arnas.klatrebackend.dataclasses.RouteSend
import com.arnas.klatrebackend.dataclasses.RouteSendDTOUpdate
import com.arnas.klatrebackend.dataclasses.ServiceResult
import com.arnas.klatrebackend.interfaces.services.RouteSendServiceInterface
import com.arnas.klatrebackend.repositories.BoulderRepository
import com.arnas.klatrebackend.repositories.RouteSendRepository
import org.springframework.stereotype.Service

@Service
class RouteSendService(
    private val routeSendRepository: RouteSendRepository,
    private val boulderRepository: BoulderRepository,
    private val imageService: ImageService,
    private val boulderService: BoulderService
): RouteSendServiceInterface
{
    override fun getUserBoulderSends(userID: Long, boulderIDs: List<Long>): ServiceResult<List<RouteSend>> {
        return try {
            ServiceResult(data = routeSendRepository.getRouteSends(userID, boulderIDs), success = true)
        } catch (e: Exception) {
            ServiceResult(success = false, message = "Error getting boulder sends", data = null)
        }
    }

    override fun addUserRouteSend(userID: Long, boulderID: Long, additionalProps: Map<String, String>) {
        routeSendRepository.insertRouteSend(userID, boulderID, additionalProps)
    }

    override fun getRouteSendByRoute(routeId: Long, userId: Long): ServiceResult<RouteSend?> {
        try {
            var routeSend = routeSendRepository.getRouteSendById(routeId, userId)
            if(routeSend == null) {
                routeSend = routeSendRepository.initializeRouteSend(routeId, userId)
            }
            return ServiceResult(success = true, data = routeSend, message = "Route send retrieved successfully")
        } catch (e: Exception) {
            e.printStackTrace()
            return ServiceResult(success = false, message = "Error getting route send: ${e.message}", data = null)
        }
    }

    override fun updateRouteSend(routeSendDTO: RouteSendDTOUpdate): ServiceResult<RouteSend> {
        try {
            val rowAffected = routeSendRepository.updateRouteSend(routeSendDTO)
            val routeSend = routeSendRepository.getRouteSendById(routeSendDTO.boulderId, routeSendDTO.userId)
            if(rowAffected <= 0) return ServiceResult(success = false, message = "Error updating route send", data = null)
            return ServiceResult(success = true, message = "Route send updated successfully", data = routeSend)
        } catch (e: Exception) {
            e.printStackTrace()
            return ServiceResult(success = false, message = "Error updating route send: ${e.message}", data = null)
        }
    }

}