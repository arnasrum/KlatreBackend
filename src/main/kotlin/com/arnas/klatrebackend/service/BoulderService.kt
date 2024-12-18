package com.arnas.klatrebackend.service

import com.arnas.klatrebackend.dataclass.User
import com.arnas.klatrebackend.repository.BoulderRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class BoulderService {

    @Autowired
    private lateinit var userService: UserService
    @Autowired
    private lateinit var boulderRepository: BoulderRepository


    fun addBoulder(access_token: String, boulderInfo: Map<String, String>): Map<String, String> {
        val userID: Int = userService.getUserID(access_token) ?: return mapOf("status" to "Invalid access_token")
        boulderRepository.addBoulder(userID, boulderInfo)
        return mapOf("status" to "OK")
    }

    fun updateBoulder(accessToken: String, boulderInfo: Map<String, String>): Map<String, Map<String, String>> {
        val user: User = userService.getUserByToken(accessToken) ?: return mapOf("0" to mapOf("status" to "Invalid Token"))
        val userBoulders = boulderRepository.getBouldersByUser(user)
        val id = boulderInfo["id"]?.toInt() ?: return mapOf("0" to mapOf("status" to "Invalid Id"))
        if (!userBoulders.keys.contains(id)) {
            return mapOf("0" to mapOf("status" to "User not authorized to edit this boulder"))
        }
        boulderRepository.updateBoulder(id, boulderInfo)
        return mapOf()
    }

}