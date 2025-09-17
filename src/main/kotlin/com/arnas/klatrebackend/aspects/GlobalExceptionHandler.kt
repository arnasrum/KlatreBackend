package com.arnas.klatrebackend.aspects

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.ErrorResponse
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.client.HttpClientErrorException

@ControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(HttpClientErrorException.Unauthorized::class)
    fun handleUnauthorizedException(exception: HttpClientErrorException.Unauthorized): ResponseEntity<ApiErrorResponse> {
        val errorResponse = ApiErrorResponse(exception.message?:  "Unauthorized", HttpStatus.UNAUTHORIZED)
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(errorResponse)
    }
}

data class ApiErrorResponse(
    val message: String,
    val status: HttpStatus
)