package com.arnas.klatrebackend.service

import com.arnas.klatrebackend.dataclass.Boulder
import com.arnas.klatrebackend.dataclass.BoulderRequest
import com.arnas.klatrebackend.dataclass.ServiceResult
import com.arnas.klatrebackend.repository.BoulderRepository
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service

@Service
class BoulderService(
    private val userService: UserService,
    private val boulderRepository: BoulderRepository,
    private val imageService: ImageService,
    private val jdbcTemplate: JdbcTemplate,
) {


    fun getBouldersByUser(userID: Long): List<Boulder> {
        val boulders = boulderRepository.getBouldersByUser(userID)
        for (boulder in boulders) {
            val image = imageService.getImage(boulder.id)
            if (image != null) {boulder.image = image.image}
        }
        return boulders
    }

    //fun addBoulder(accessToken: String, boulderInfo: Map<String, String>): Map<String, String> {
    //    val userID: Int = userService.getUserID(accessToken) ?: return mapOf("status" to "Invalid access token")
    //    val boulderID: Long = boulderRepository.addBoulder(userID, boulderInfo)
    //    return mapOf("status" to "OK", "boulderID" to boulderID.toString())
    //}

    fun updateBoulder(userID: Long, boulderInfo: Map<String, String>): Map<String, String> {
        val userBoulders = boulderRepository.getBouldersByUser(userID)
        println(userBoulders.filter { it.id == boulderInfo["id"]?.toLong() })
        boulderRepository.updateBoulder(boulderInfo)
        return mapOf("status" to "OK")
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

    open fun getBouldersByPlace(placeID: Long): ServiceResult<Array<Boulder>> {
        val boulders = boulderRepository.getBouldersByPlace(placeID)
        boulders.forEach { imageService.getImage(it.id)}
        return ServiceResult(boulders, true)
    }

    open fun addBoulderToPlace(userID: Long, placeID: Long, boulderInfo: Map<String, String>): ServiceResult<Long> {

        val userBoulders = boulderRepository.getBouldersByUser(userID)

        val name = boulderInfo["name"]
            ?: return ServiceResult(success = false, message = "Missing required field: name")
        val attemptsString = boulderInfo["attempts"]
            ?: return ServiceResult(success = false, message = "Missing required field: attempts")
        val grade = boulderInfo["grade"]
            ?: return ServiceResult(success = false, message = "Missing required field: grade")

        val attempts = try {
            attemptsString.toInt()
        } catch (e: NumberFormatException) {
            return ServiceResult(success = false, message = "Invalid attempts format: must be a valid integer")
        }

        if (name.isBlank()) {
            return ServiceResult(success = false, message = "Boulder name cannot be blank")
        }
        if (grade.isBlank()) {
            return ServiceResult(success = false, message = "Boulder grade cannot be blank")
        }
        if (attempts < 0) {
            return ServiceResult(success = false, message = "Attempts cannot be negative")
        }

        val boulder = BoulderRequest(
            name = name,
            attempts = attempts,
            grade = grade,
            place = placeID,
        )
        val boulderID = boulderRepository.addBoulder(userID, boulder)

        return ServiceResult(success = true, message = "Boulder added successfully", data = boulderID)
    }

}