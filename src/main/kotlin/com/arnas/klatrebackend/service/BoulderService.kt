package com.arnas.klatrebackend.service

import com.arnas.klatrebackend.dataclass.Boulder
import com.arnas.klatrebackend.dataclass.BoulderRequest
import com.arnas.klatrebackend.dataclass.BoulderWithSend
import com.arnas.klatrebackend.dataclass.RouteSend
import com.arnas.klatrebackend.dataclass.ServiceResult
import com.arnas.klatrebackend.repository.BoulderRepository
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service

@Service
class BoulderService(
    private val boulderRepository: BoulderRepository,
    private val imageService: ImageService,
) {


    fun getBouldersByUser(userID: Long): List<Boulder> {
        val boulders = boulderRepository.getBouldersByUser(userID)
        for (boulder in boulders) {
            val image = imageService.getImage(boulder.id)
            if (image != null) {boulder.image = image.image}
        }
        return boulders
    }

    fun updateBoulder(userID: Long, boulderInfo: Map<String, String>): ServiceResult<String> {
        // Check if user has permission to update boulder
        boulderRepository.updateBoulder(boulderInfo)
        if(boulderInfo.containsKey("image")) {
            imageService.updateImage(boulderInfo["boulderID"]!!.toLong(), boulderInfo["image"]!!)
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

    open fun getUserBoulderSends(userID: Long, boulderIDs: List<Long>): ServiceResult<Array<RouteSend>> {
        return try {
            ServiceResult(data = boulderRepository.getBoulderSends(userID, boulderIDs), success = true)
        } catch (e: Exception) {
            ServiceResult(success = false, message = "Error getting boulder sends", data = null)
        }
    }

    open fun getBouldersWithSendsByPlace(userID: Long, placeID: Long): ServiceResult<List<BoulderWithSend>> {
        return try {
            val boulders = boulderRepository.getBouldersByPlace(placeID)
            val routeSends = boulderRepository.getBoulderSends(userID, boulders.map { it.id })
            println("routeSends: $routeSends")
            routeSends.forEach { println("routeSend: $it") }

            val bouldersWithSends = boulders.map { boulder ->
                val send = routeSends.filter { boulder.id == it.boulderID }
                println("send: $send")
                imageService.getImage(boulder.id)?.let {boulder.image = it.image}
                BoulderWithSend(
                    boulder = boulder,
                    send = send.firstOrNull()
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