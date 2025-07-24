package com.arnas.klatrebackend.service

import com.arnas.klatrebackend.dataclass.Boulder
import com.arnas.klatrebackend.repository.BoulderRepository
import org.springframework.stereotype.Service

@Service
class BoulderService(
    private val userService: UserService,
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

    fun addBoulder(accessToken: String, boulderInfo: Map<String, String>): Map<String, String> {
        val userID: Int = userService.getUserID(accessToken) ?: return mapOf("status" to "Invalid access token")
        val boulderID: Long = boulderRepository.addBoulder(userID, boulderInfo)
        return mapOf("status" to "OK", "boulderID" to boulderID.toString())
    }

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

}