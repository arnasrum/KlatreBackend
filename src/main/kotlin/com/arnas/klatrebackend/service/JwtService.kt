package com.arnas.klatrebackend.service

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import org.springframework.stereotype.Service
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.Jwts
import org.springframework.beans.factory.annotation.Value
import java.util.Date

@Service
class JwtService(
    @param:Value("\${JWT_SECRET}") private val googleSecret: String,
) {

    open fun createJwtToken(subject: String, claims: Map<String, String>): String {
        val jwtBuilder = Jwts.builder()
        claims.forEach { (name, value) -> jwtBuilder.claim(name, value) }
        val jwt = jwtBuilder
            .subject(subject)
            .issuedAt(Date.from(Date().toInstant()))
            .expiration(Date.from(Date().toInstant().plusSeconds(60 * 60 * 24)))
            .signWith(Keys.hmacShaKeyFor(googleSecret.toByteArray()))
            .compact()
        return jwt
    }

    open fun decodeJwt(jwt: String): Jws<Claims> {
        val jwtParser = Jwts.parser()
            .verifyWith(Keys.hmacShaKeyFor(googleSecret.toByteArray()))
            .build()
        val claims = jwtParser.parseSignedClaims(jwt)
        return claims
    }

    open fun getJwtPayload(claims: Jws<Claims>): Map<String, String> {
        val payload = mutableMapOf<String, String>()
        claims.payload.forEach { (name: String, value: Any) ->
            payload[name] = value.toString()
        }
        return payload
    }

}