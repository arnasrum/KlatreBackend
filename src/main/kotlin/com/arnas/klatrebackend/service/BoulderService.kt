package com.arnas.klatrebackend.service

import com.arnas.klatrebackend.dataclass.Boulder
import com.arnas.klatrebackend.dataclass.User
import com.arnas.klatrebackend.repository.BoulderRepository
import com.arnas.klatrebackend.service.ImageService
import org.springframework.stereotype.Service

@Service
class BoulderService(private val userService: UserService,
    private val boulderRepository: BoulderRepository,
    private val imageService: ImageService
) {


    fun getBouldersByUser(user: User): List<Boulder> {
        val boulders = boulderRepository.getBouldersByUser(user)
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

    fun updateBoulder(accessToken: String, boulderInfo: Map<String, String>): Map<String, String> {
        val user: User = userService.getUserByToken(accessToken) ?: return mapOf("status" to "Invalid Token")
        val userBoulders = boulderRepository.getBouldersByUser(user)
        val id = boulderInfo["id"]?.toInt() ?: return mapOf("status" to "Invalid Id")
        //if (!userBoulders.keys.contains(id)) {
            //return mapOf("0" to mapOf("status" to "User not authorized to edit this boulder"))
        //}
        boulderRepository.updateBoulder(id, boulderInfo)
        return mapOf()
    }

}