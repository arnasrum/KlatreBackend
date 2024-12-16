package com.arnas.klatrebackend.controller

import com.arnas.klatrebackend.service.LoginService
import com.arnas.klatrebackend.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@CrossOrigin
@RestController
class OAuthController {

    @Autowired
    lateinit var loginService: LoginService
    @Autowired
    lateinit var userService: UserService

    @PostMapping("/google_login")
    fun googleLogin(@RequestBody requestBody: Map<String, String>): String {
        val token = requestBody["code"]
        if (token.isNullOrBlank()) {
            return "invalid token"
        }
        loginService.login(token)
        return "success"
    }

    @GetMapping("/google_login")
    fun getGoogleLogin(@RequestParam token: String): String {

        userService.getGoogleUserProfile(token)


        return "success"
    }


    @GetMapping("/test")
    fun testAPI(): String {
        return "dette fungerer"
    }
    @PostMapping("/test")
    fun testAPIPost(@RequestBody requestBody: Map<String, String>): String {
        println(requestBody)
        return "dette fungerer"
    }

}