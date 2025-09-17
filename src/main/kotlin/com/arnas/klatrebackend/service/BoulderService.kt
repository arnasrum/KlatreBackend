package com.arnas.klatrebackend.service

import com.arnas.klatrebackend.dataclass.BoulderRequest
import com.arnas.klatrebackend.dataclass.BoulderWithSend
import com.arnas.klatrebackend.dataclass.RouteSend
import com.arnas.klatrebackend.dataclass.ServiceResult
import com.arnas.klatrebackend.interfaces.services.BoulderServiceInterface
import com.arnas.klatrebackend.repository.BoulderRepository
import com.arnas.klatrebackend.repository.RouteSendRepository
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class BoulderService(
    private val boulderRepository: BoulderRepository,
    private val imageService: ImageService,
    private val routeSendRepository: RouteSendRepository,
): BoulderServiceInterface {


    override fun addBoulder(userId: Long, placeId: Long, boulderInfo: Map<String, String>): ServiceResult<Long> {

        val name = boulderInfo["name"]
            ?: return ServiceResult(success = false, message = "Missing required field: name")
        val grade = boulderInfo["grade"]
            ?: return ServiceResult(success = false, message = "Missing required field: grade")

        if (name.isBlank()) {
            return ServiceResult(success = false, message = "Boulder name cannot be blank")
        }
        if (grade.isBlank()) {
            return ServiceResult(success = false, message = "Boulder grade cannot be blank")
        }

        val boulderRequest = BoulderRequest(
            name = name,
            grade = grade,
            place = placeId,
        )
        val boulderID = boulderRepository.addBoulder(userId, boulderRequest)

        return ServiceResult(success = true, message = "Boulder added successfully", data = boulderID)
    }

   override fun updateBoulder(boulderId: Long, userId: Long, boulderInfo: Map<String, String>, image: MultipartFile?): ServiceResult<String> {
        // Check if user has permission to update boulder
        boulderRepository.updateBoulder(boulderInfo.filterKeys { it != "image" })
        image?.let {
            imageService.storeImageFile(image, boulderId, userId)
        }
        return ServiceResult(success = true, message = "Boulder updated successfully")
    }

    override fun deleteBoulder(boulderId: Long): ServiceResult<Unit> {
        try {
            imageService.getImage(boulderId)?.let { imageService.deleteImage(it) }
            boulderRepository.deleteBoulder(boulderId)
            return ServiceResult(success = true, message = "Boulder deleted successfully")
        } catch (e: Exception) {
            return ServiceResult(success = false, message = "Error deleting boulder", data = null)
        }
    }

    open fun getUserBoulderSends(userID: Long, boulderIDs: List<Long>): ServiceResult<List<RouteSend>> {
        return try {
            ServiceResult(data = routeSendRepository.getBoulderSends(userID, boulderIDs), success = true)
        } catch (e: Exception) {
            ServiceResult(success = false, message = "Error getting boulder sends", data = null)
        }
    }

    open fun getBouldersWithSendsByPlace(userID: Long, placeID: Long): ServiceResult<List<BoulderWithSend>> {
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

    open fun addUserRouteSend(userID: Long, boulderID: Long, additionalProps: Map<String, String> = emptyMap()) {
        routeSendRepository.insertRouteSend(userID, boulderID, additionalProps)
    }

}