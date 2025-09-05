package com.arnas.klatrebackend.service

import com.arnas.klatrebackend.dataclass.Boulder
import com.arnas.klatrebackend.dataclass.BoulderRequest
import com.arnas.klatrebackend.dataclass.BoulderWithSend
import com.arnas.klatrebackend.dataclass.Image
import com.arnas.klatrebackend.dataclass.RouteSend
import com.arnas.klatrebackend.dataclass.ServiceResult
import com.arnas.klatrebackend.repository.BoulderRepository
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class BoulderService(
    private val boulderRepository: BoulderRepository,
    private val imageService: ImageService,
) {


    fun getBouldersByUser(userID: Long): List<Boulder> {
        val boulders = boulderRepository.getBouldersByUser(userID)
        for (boulder in boulders) {
            val image = imageService.getImage(boulder.id)
            if (image != null) {boulder.image = image.getUrl()}
        }
        return boulders
    }

    fun updateBoulder(boulderID: Long, userID: Long, boulderInfo: Map<String, String>, image: MultipartFile?): ServiceResult<String> {
        // Check if user has permission to update boulder
        println("Checking if user has permission to update boulder")
        boulderRepository.updateBoulder(boulderInfo.filterKeys { it != "image" })
        image?.let {
            println("image included")
            imageService.storeImageFile(image, boulderID, "16/9", userID)
        }
        return ServiceResult(success = true, message = "Boulder updated successfully")
    }

    fun deleteBoulder(userID: Long, boulderID: Long) : Map<String, String> {
        val usersBoulders = getBouldersByUser(userID)
        usersBoulders.any { it.id == boulderID } || return mapOf("status" to "401", "message" to "Unauthorized")
        val image = imageService.getImage(boulderID)
        if(image != null) {
            imageService.deleteImage(image)
        }
        boulderRepository.deleteBoulder(boulderID)

        return mapOf("status" to "200", "message" to "Image deleted successfully")
    }

    open fun addBoulderToPlace(userID: Long, placeID: Long, boulderInfo: Map<String, String>): ServiceResult<Long> {

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
            place = placeID,
        )
        val boulderID = boulderRepository.addBoulder(userID, boulderRequest)

        return ServiceResult(success = true, message = "Boulder added successfully", data = boulderID)
    }

    open fun getUserBoulderSends(userID: Long, boulderIDs: List<Long>): ServiceResult<List<RouteSend>> {
        return try {
            ServiceResult(data = boulderRepository.getBoulderSends(userID, boulderIDs), success = true)
        } catch (e: Exception) {
            ServiceResult(success = false, message = "Error getting boulder sends", data = null)
        }
    }

    open fun getBouldersWithSendsByPlace(userID: Long, placeID: Long): ServiceResult<List<BoulderWithSend>> {
        return try {
            val boulders = boulderRepository.getBouldersByPlace(placeID)
            val boulderIDs = boulders.map { it.id }
            if(boulderIDs.isEmpty()) return ServiceResult(success = true, data = emptyList(), message = "No boulders found in this place")
            val routeSends = boulderRepository.getBoulderSends(userID, boulderIDs)
            val bouldersWithSends = boulders.map { boulder ->
                val send = routeSends.filter { boulder.id == it.boulderID }
                imageService.getImageMetadataByBoulder(boulder.id).data.let {boulder.image = "http://localhost:8080${it?.getUrl()}"}
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


        boulderRepository.insertRouteSend(userID, boulderID, additionalProps)
    }

}