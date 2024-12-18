package com.arnas.klatrebackend.controller

import com.arnas.klatrebackend.dataclass.User
import com.arnas.klatrebackend.repository.BoulderRepository
import com.arnas.klatrebackend.service.BoulderService
import com.arnas.klatrebackend.service.UserService
import org.json.JSONObject
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin(origins = arrayOf("http://localhost:5173"))
class BoulderController(
    private val userService: UserService,
    private val boulderRepository: BoulderRepository,
    private val boulderService: BoulderService
) {

    @GetMapping("/boulders")
    fun getBoulders(@RequestParam access_token: String): Map<Int, Map<String, String>> {
        val user: User = userService.getUserByToken(access_token) ?: return mapOf(
            0 to mapOf("status" to "Invalid access_token")
        )
        val json = boulderRepository.getBouldersByUser(user)
        println(json)
        return json
    }



    @PostMapping("/boulder")
    fun postBoulder(@RequestParam access_token: String, @RequestBody requestBody: Map<String, String>): Map<String, String> {
        boulderService.addBoulder(access_token, requestBody)
        return mapOf("status" to "OK")
    }

    @PutMapping("/boulder")
    fun putBoulder(@RequestParam access_token: String, @RequestBody requestBody: Map<String, String>): Map<String, String> {
        val userID: Int = userService.getUserID(access_token) ?: return mapOf("status" to "Invalid access_token")
        boulderService.updateBoulder(access_token, requestBody)
        return mapOf("status" to "OK")
    }


}