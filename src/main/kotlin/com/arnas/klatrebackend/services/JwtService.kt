package com.arnas.klatrebackend.services

import com.arnas.klatrebackend.interfaces.services.JwtServiceInterface
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import org.springframework.stereotype.Service
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.Jwts
import org.springframework.beans.factory.annotation.Value
import java.util.Date

@Service
class JwtService(
    @param:Value("\${JWT_SECRET}") private val jwtSecret: String,
): JwtServiceInterface {

    override fun createJwtToken(subject: String, claims: Map<String, String>): String {
        val jwtBuilder = Jwts.builder()
        claims.forEach { (name, value) -> jwtBuilder.claim(name, value) }
        val jwt = jwtBuilder
            .subject(subject)
            .issuedAt(Date.from(Date().toInstant()))
            .expiration(Date.from(Date().toInstant().plusSeconds(60 * 60 * 24)))
            .signWith(Keys.hmacShaKeyFor(jwtSecret.toByteArray()))
            .compact()
        return jwt
    }

    override fun decodeJwt(jwt: String): Jws<Claims> {
        val jwtParser = Jwts.parser()
            .verifyWith(Keys.hmacShaKeyFor(jwtSecret.toByteArray()))
            .build()
        val claims = jwtParser.parseSignedClaims(jwt)
        return claims
    }

    override fun getJwtPayload(claims: Jws<Claims>): Map<String, String> {
        val payload = mutableMapOf<String, String>()
        claims.payload.forEach { (name: String, value: Any) ->
            payload[name] = value.toString()
        }
        return payload
    }

    override fun validateJwtToken(token: String): Boolean {
        return try {
            val claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(jwtSecret.toByteArray()))
                .build()
                .parseSignedClaims(token)
            !claims.payload.expiration.before(Date())
        } catch (e: Exception) {
            false
        }
    }

    fun extractUserId(token: String): String? {
        return try {
            val claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(jwtSecret.toByteArray()))
                .build()
                .parseSignedClaims(token)
            claims.payload.subject
        } catch (e: Exception) {
            null
        }
    }

    fun extractExpiration(token: String): Date {
        val claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(jwtSecret.toByteArray()))
                .build()
                .parseSignedClaims(token)
        return claims.payload.expiration
    }

    override fun refreshToken(token: String): String? {
        return try {
            val claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(jwtSecret.toByteArray()))
                .build()
                .parseSignedClaims(token)
                .payload

            val userId = claims.subject
            val email = claims["email"] as? String
            val name = claims["name"] as? String
            val id = claims["id"] as? String

            if (userId != null && email != null && name != null && id != null) {
                createJwtToken(userId, mapOf("email" to email, "name" to name, "id" to id))
            } else null
        } catch (e: Exception) {
            null
        }
    }
}