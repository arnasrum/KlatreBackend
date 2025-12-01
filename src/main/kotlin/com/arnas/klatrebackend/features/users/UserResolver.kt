package com.arnas.klatrebackend.features.users

import com.arnas.klatrebackend.features.auth.jwt.JwtService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.MethodParameter
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import org.springframework.web.server.ResponseStatusException

@Component
class UserResolver(
    private val jwtService: JwtService
) : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.parameterType == User::class.java
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any? {
        val request = webRequest.getNativeRequest(HttpServletRequest::class.java)!!

        val token = request.cookies?.firstOrNull { it.name == "authToken" }?.value
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing authentication token")

        val userInfo = try {
            jwtService.getJwtPayload(jwtService.decodeJwt(token))
        } catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token")
        }

        val userId = userInfo["sub"]?.toLongOrNull()
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid user ID")
        val email = userInfo["email"] ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing email")
        val name = userInfo["name"] ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing name")

        return User(userId, email, name)
    }
}