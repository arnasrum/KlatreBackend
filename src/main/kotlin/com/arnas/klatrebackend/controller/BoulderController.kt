package com.arnas.klatrebackend.controller

import com.arnas.klatrebackend.dataclass.User
import com.arnas.klatrebackend.repository.BoulderRepository
import com.arnas.klatrebackend.service.UserService
import org.json.JSONObject
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class BoulderController(
    private val userService: UserService,
    private val boulderRepository: BoulderRepository
) {

    @GetMapping("/boulders")
    fun getBoulders(@RequestParam access_token: String): Map<Int, Map<String, String>> {
        val user: User = userService.getUserByToken(access_token) ?: return mapOf(
            0 to mapOf("status" to "Invalid access_token")
        )
        val json = boulderRepository.getBouldersByUser(user)
        return json
    }



}