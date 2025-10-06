package com.arnas.klatrebackend.controllers

import com.arnas.klatrebackend.interfaces.services.LoginServiceInterface
import org.springframework.web.bind.annotation.*
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseCookie
import java.time.Duration
import java.time.ZonedDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

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
                // Calculate expiration in UTC
                val expiresAt = ZonedDateTime.now(ZoneOffset.UTC).plusHours(1)
                
                val cookie = ResponseCookie.from("authToken", jwt)
                    .maxAge(Duration.ofHours(1))
                    .path("/")
                    .httpOnly(false)
                    .secure(false)
                    .sameSite("Lax")
                    .build()
                
                ResponseEntity.ok()
                    .header("Set-Cookie", cookie.toString())
                    .header("Date", DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now(ZoneOffset.UTC)))
                    .body(mapOf("access_token" to jwt))
            } else {
                ResponseEntity.badRequest().body(mapOf("error" to "Authentication failed"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return ResponseEntity.internalServerError().body(mapOf("error" to "Internal server error"))
        }
    }
}