package com.arnas.klatrebackend.components

import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.slf4j.Logger
import org.springframework.dao.DataAccessException
import org.springframework.data.rest.webmvc.ResourceNotFoundException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice



@RestControllerAdvice
class RestControllerExceptionHandler {

    val logger: Logger = LoggerFactory.getLogger(this::class.java)
    fun logHttpEvent(request: HttpServletRequest, exception: Exception) {
        val method = request.method
        val uri = request.requestURI
        val clientIp = request.remoteAddr
        val queryParams = request.queryString ?: "N/A"
        logger.error(
            "Unhandled exception. Request failed. METHOD: {}, URI: {}, IP: {}, Query: {}",
            method, uri, clientIp, queryParams, exception
        )
    }

    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleResourceNotFoundException(
        exception: ResourceNotFoundException,
        request: HttpServletRequest
    ): ResponseEntity<Map<String, Any>> {
        val message = "Resource not found"
        val response: ResponseEntity<Map<String, Any>> = ResponseEntity.status(404).body(mapOf("message" to message))
        logHttpEvent(request, exception)
        return response
    }

    @ExceptionHandler(Exception::class)
    fun handleException(
        request: HttpServletRequest,
        exception: Exception
    ): ResponseEntity<Map<String, Any>> {
        val message = "Internal server error"
        val response: ResponseEntity<Map<String, Any>> = ResponseEntity.status(500).body(mapOf("message" to message))
        logHttpEvent(request, exception)
        return response
    }

    @ExceptionHandler(RuntimeException::class)
    fun handleException(
        request: HttpServletRequest,
        exception: RuntimeException
    ): ResponseEntity<Map<String, Any>> {
        val message = "Internal server error"
        val response: ResponseEntity<Map<String, Any>> = ResponseEntity.status(500).body(mapOf("message" to message))
        logHttpEvent(request, exception)
        return response
    }

    @ExceptionHandler(DataAccessException::class)
    fun handleException(
        request: HttpServletRequest,
        exception: DataAccessException
    ): ResponseEntity<Map<String, Any>> {
        val message = exception.message ?: "Internal server error"
        val response: ResponseEntity<Map<String, Any>> = ResponseEntity.status(500).body(mapOf("message" to message))
        logHttpEvent(request, exception)
        return response
    }
}