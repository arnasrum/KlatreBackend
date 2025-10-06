package com.arnas.klatrebackend.components

import com.arnas.klatrebackend.services.JwtService
import com.arnas.klatrebackend.services.UserService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.*

@Component
class JwtAuthenticationFilter(
    private val jwtService: JwtService,
    private val userService: UserService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val jwt = extractJwtFromCookie(request)
            
            if (jwt != null && jwtService.validateToken(jwt)) {
                val userId = jwtService.extractUserId(jwt)
                
                if (userId != null && SecurityContextHolder.getContext().authentication == null) {
                    val serviceResult = userService.getUserById(userId.toLong())
                    val user = serviceResult.data
                    if (!serviceResult.success || user != null) {
                        val authentication = UsernamePasswordAuthenticationToken(
                            user, null, emptyList()
                        )
                        authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
                        SecurityContextHolder.getContext().authentication = authentication
                        
                        if (shouldRefreshToken(jwt)) {
                            val newJwt = jwtService.refreshToken(jwt)
                            if (newJwt != null) {
                                setJwtCookie(response, newJwt)
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            logger.error("Cannot set user authentication: ${e.message}")
        }
        
        filterChain.doFilter(request, response)
    }
    
    private fun extractJwtFromCookie(request: HttpServletRequest): String? {
        return request.cookies?.firstOrNull { it.name == "authToken" }?.value
    }
    
    private fun shouldRefreshToken(jwt: String): Boolean {
        val expirationDate = jwtService.extractExpiration(jwt)
        val now = Date()
        val timeUntilExpiry = expirationDate.time - now.time
        val oneHourInMillis = 60 * 60 * 1000L
        
        return timeUntilExpiry < oneHourInMillis
    }
    
    private fun setJwtCookie(response: HttpServletResponse, jwt: String) {
        response.addHeader("Set-Cookie",
            "authToken=$jwt; Path=/; Max-Age=${60 * 60}; HttpOnly; SameSite=Lax")
    }
}