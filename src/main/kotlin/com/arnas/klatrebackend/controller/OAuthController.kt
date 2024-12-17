package com.arnas.klatrebackend.controller

import com.arnas.klatrebackend.service.LoginService
import com.arnas.klatrebackend.service.UserService
import com.nimbusds.jose.shaded.gson.JsonObject
import com.nimbusds.jose.shaded.gson.JsonParser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@CrossOrigin
@RestController
class OAuthController {

    @Autowired
    lateinit var loginService: LoginService
    @Autowired
    lateinit var userService: UserService

    @PostMapping("/old")
    fun googleLogin(@RequestBody requestBody: Map<String, String>): String {
        val token = requestBody["code"]
        if (token.isNullOrBlank()) {
            return "invalid token"
        }
        loginService.login(token)
        return "success"
    }

    @PostMapping("/google_login")
    fun getGoogleLogin(@RequestBody requestBody: Map<String, String>): String {
        val token = requestBody["token"]
        if (token.isNullOrBlank()) {
            return "invalid token"
        }
        userService.getGoogleUserProfile(token)
        return "success"
    }
}