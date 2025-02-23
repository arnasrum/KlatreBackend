package com.arnas.klatrebackend.controller

import com.arnas.klatrebackend.dataclass.Boulder
import com.arnas.klatrebackend.dataclass.User
import com.arnas.klatrebackend.repository.BoulderRepository
import com.arnas.klatrebackend.service.BoulderService
import com.arnas.klatrebackend.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
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
    fun getBoulders(@RequestParam access_token: String): ResponseEntity<List<Boulder>> {
        val user: User = userService.getUserByToken(access_token) ?:
            return ResponseEntity(HttpStatus.UNAUTHORIZED)
        val json = boulderService.getBouldersByUser(user)
        //println(json)
        return ResponseEntity(json, HttpStatus.OK)
    }



    @PostMapping("/boulder")
    fun postBoulder(@RequestParam accessToken: String, @RequestBody requestBody: Map<String, String>): Map<String, String> {
        boulderService.addBoulder(accessToken, requestBody)
        return mapOf("status" to "OK")
    }

    @PutMapping("/boulder")
    fun putBoulder(@RequestParam accessToken: String, @RequestBody requestBody: Map<String, String>): Map<String, String> {
        val userID: Int = userService.getUserID(accessToken) ?: return mapOf("status" to "Invalid access_token")
        boulderService.updateBoulder(accessToken, requestBody)
        return mapOf("status" to "OK")
    }


}