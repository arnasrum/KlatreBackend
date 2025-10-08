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
import java.security.SecureRandom
import java.security.MessageDigest
import java.util.Base64


@RestController
@RequestMapping("/api/oauth2")
class OAuthController(
    private val loginService: LoginServiceInterface,
    private val redisTemplate: RedisTemplate<String, String>
) {

    @Value("\${GOOGLE_CLIENT_ID}")
    private lateinit var googleClientId: String


    @GetMapping("/authorization/google")
    fun googleAuthorization(
        @RequestHeader("referer") referer: String?
    ): RedirectView {
        val redirectUri = "http://localhost:8080/api/oauth2/code"
        val scope = "openid profile email"

        val state = java.util.UUID.randomUUID().toString()
        val codeVerifier = generateCodeVerifier()
        val codeChallenge = hashString(codeVerifier, "SHA-256")

        redisTemplate.opsForValue().set(
            "oauth_state:$state?origin",
            referer ?: "default",
            10,
            TimeUnit.MINUTES
        )
        redisTemplate.opsForValue().set(
            "oauth_state:$state?codeVerifier",
            codeVerifier,
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
                "&state=$state" +
                "&code_challenge=$codeChallenge" +
                "&code_challenge_method=S256"

        return RedirectView(authUrl)
    }

    @GetMapping("/code")
    fun handleCallback(
        @RequestParam code: String,
        @RequestParam state: String,
        response: HttpServletResponse
    ): RedirectView {
        val origin = redisTemplate.opsForValue().get("oauth_state:$state?origin")
            ?: return RedirectView("http://localhost:5173/login?error=invalid_state")
        redisTemplate.delete("oauth_state:$state?origin")
        val codeVerifier = redisTemplate.opsForValue().get("oauth_state:$state?codeVerifier")
        redisTemplate.delete("oauth_state:$state?codeVerifier")

        val jwt = loginService.getJWTToken(code, codeVerifier)
        
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

    private fun generateCodeVerifier(): String {
        val random = SecureRandom.getInstance("SHA1PRNG")
        val bytes = ByteArray(64).apply { random.nextBytes(this) }
        val codeVerifier = Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(bytes)
        return codeVerifier
    }

    private fun hashString(string: String, hashingAlgorithm: String): String {
        val hashedString = MessageDigest.getInstance(hashingAlgorithm)
            .digest(string.toByteArray(Charsets.US_ASCII))
            .let {
                Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(it)
            }
        return hashedString
    }



}