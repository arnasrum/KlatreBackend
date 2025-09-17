package com.arnas.klatrebackend.controller

import com.arnas.klatrebackend.interfaces.services.LoginServiceInterface
import com.arnas.klatrebackend.service.LoginService
import org.springframework.web.bind.annotation.*
import org.springframework.http.ResponseEntity

@RestController
class OAuthController(
    private val loginService: LoginServiceInterface,
) {

    @PostMapping("/old")
    fun googleLogin(@RequestBody requestBody: Map<String, String>): String {
        val token = requestBody["code"]
        if (token.isNullOrBlank()) {
            return "invalid token"
        }
        return "success"
    }

    @PostMapping("/google_login")
    fun getGoogleLogin(@RequestBody requestBody: Map<String, String>): String {
        val token = requestBody["token"]
        if (token.isNullOrBlank()) {
            return "invalid token"
        }
        //userService.getGoogleUserProfile(token)
        return "success"
    }

    @PostMapping("/google_oauth_exchange")
    open fun googleOAuthExchange(@RequestBody requestBody: Map<String, String>): ResponseEntity<Map<String, String>> {
        val code = requestBody["code"]
        if (code.isNullOrBlank()) {
            return ResponseEntity.badRequest().body(mapOf("error" to "Invalid or missing code"))
        }
        
        try {
            val jwt = loginService.getJWTToken(code)
            
            return if (jwt != null) {
                ResponseEntity.ok(mapOf("access_token" to jwt))
            } else {
                ResponseEntity.badRequest().body(mapOf("error" to "Authentication failed"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return ResponseEntity.internalServerError().body(mapOf("error" to "Internal server error"))
        }
    }
}