package com.arnas.klatrebackend.controller

import com.arnas.klatrebackend.dataclass.ServiceResult
import com.arnas.klatrebackend.service.JwtService
import com.arnas.klatrebackend.service.LoginService
import com.arnas.klatrebackend.service.UserService
import com.nimbusds.jose.shaded.gson.JsonObject
import com.nimbusds.jose.shaded.gson.JsonParser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import kotlin.math.log
import org.springframework.http.ResponseEntity

@CrossOrigin(origins = ["http://localhost:5173"])
@RestController
class OAuthController(
    private val loginService: LoginService,
) {

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
            return ResponseEntity.internalServerError().body(mapOf("error" to "Internal server error"))
        }
    }
}