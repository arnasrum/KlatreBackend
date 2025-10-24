package com.arnas.klatrebackend.components

import org.springframework.data.rest.webmvc.ResourceNotFoundException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class RestControllerExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleResourceNotFoundException(exception: ResourceNotFoundException): ResponseEntity<Map<String, Any>> {
        val message = exception.message?: "Resource not found"
        val response: ResponseEntity<Map<String, Any>> = ResponseEntity.status(404).body(mapOf("message" to message))
        return response
    }

    @ExceptionHandler(Exception::class)
    fun handleException(exception: Exception): ResponseEntity<Map<String, Any>> {
        val message = exception.message?: "Internal server error"
        val response: ResponseEntity<Map<String, Any>> = ResponseEntity.status(500).body(mapOf("message" to message))
        return response
    }

    @ExceptionHandler(RuntimeException::class)
    fun handleException(exception: RuntimeException): ResponseEntity<Map<String, Any>> {
        val message = exception.message ?: "Internal server error"
        val response: ResponseEntity<Map<String, Any>> = ResponseEntity.status(500).body(mapOf("message" to message))
        return response
    }

}