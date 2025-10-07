package com.arnas.klatrebackend.controllers

import com.arnas.klatrebackend.dataclasses.User
import com.arnas.klatrebackend.interfaces.services.LoginServiceInterface
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.*
import org.springframework.http.ResponseEntity
import org.springframework.web.servlet.view.RedirectView
import org.springframework.data.redis.core.RedisTemplate
import java.util.concurrent.TimeUnit
import jakarta.servlet.http.HttpServletResponse


@RestController
@RequestMapping("/api/oauth2")
class OAuthController(
    private val loginService: LoginServiceInterface,
    private val redisTemplate: RedisTemplate<String, String>
) {

    @Value("\${GOOGLE_CLIENT_ID}")
    private lateinit var googleClientId: String


    @GetMapping("/authorization/google")
    fun googleAuthorization(@RequestParam(required = false) origin: String?): RedirectView {
        val redirectUri = "http://localhost:8080/api/oauth2/code"
        val scope = "openid profile email"

        val state = java.util.UUID.randomUUID().toString()
        redisTemplate.opsForValue().set(
            "oauth_state:$state",
            origin ?: "default",
            10,
            TimeUnit.MINUTES
        )

        val authUrl = "https://accounts.google.com/o/oauth2/v2/auth" +
                "?client_id=$googleClientId" +
                "&redirect_uri=$redirectUri" +
                "&response_type=code" +
                "&scope=$scope" +
                "&access_type=offline" +
                "&prompt=consent" +
                "&state=$state"

        return RedirectView(authUrl)
    }

    @GetMapping("/code")
    fun handleCallback(
        @RequestParam code: String,
        @RequestParam state: String,
        response: HttpServletResponse
    ): RedirectView {
        val origin = redisTemplate.opsForValue().get("oauth_state:$state")
            ?: return RedirectView("http://localhost:5173/login?error=invalid_state")

        redisTemplate.delete("oauth_state:$state")

        val jwt = loginService.getJWTToken(code)
        
        response.addHeader("Set-Cookie",
            "authToken=$jwt; Path=/; Max-Age=${60 * 60}; HttpOnly; SameSite=Lax")
        return RedirectView(origin)
    }

    @GetMapping("/login")
    fun getUser(user: User): ResponseEntity<out Any> {
        return ResponseEntity.ok(mapOf("user" to user))
    }

    @PostMapping("/logout")
    fun logout(response: HttpServletResponse): ResponseEntity<String> {
        response.addHeader("Set-Cookie", 
            "authToken=; Path=/; Max-Age=-1; HttpOnly; SameSite=Lax")
        return ResponseEntity.ok("Logged out successfully")
    }


}