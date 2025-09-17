package com.arnas.klatrebackend.interfaces.services

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws

interface JwtServiceInterface {
    fun createJwtToken(subject: String, claims: Map<String, String>): String
    fun decodeJwt(jwt: String): Jws<Claims>
    fun getJwtPayload(claims: Jws<Claims>): Map<String, String>
}