package com.arnas.klatrebackend.controllers

import com.arnas.klatrebackend.dataclasses.ClimbingSession
import com.arnas.klatrebackend.dataclasses.ClimbingSessionDTO
import com.arnas.klatrebackend.dataclasses.RouteAttempt
import com.arnas.klatrebackend.dataclasses.User
import com.arnas.klatrebackend.services.ClimbingSessionService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/climbingSessions")
class ClimbingSessionController(
    private val climbingSessionService: ClimbingSessionService,
) {

    @GetMapping()
    fun getPastSessions(@RequestParam groupId: Long, user: User): ResponseEntity<out Any> {
        val serviceResult = climbingSessionService.getSessionsByGroup(groupId, user.id)
        if(!serviceResult.success) return ResponseEntity.badRequest().body(mapOf("message" to serviceResult.message))
        return ResponseEntity.ok(mapOf("data" to serviceResult.data))
    }


    data class AddSessionRequest(
        val groupId: Long,
        val placeId: Long,
        val startDate: String,
        val routeAttempts: List<RouteAttempt>

    )

    @PostMapping()
    fun uploadSession(@RequestBody request: AddSessionRequest, user: User): ResponseEntity<out Any> {
        val climbingSession = ClimbingSessionDTO(request.groupId, user.id, request.placeId, request.startDate, null, request.routeAttempts)
        climbingSession.routeAttempts.isEmpty().let {
            if (it) return ResponseEntity.badRequest().body(mapOf("message" to "Session must have at least one attempt"))
        }
        println(climbingSession)
        val sessionWithUserId = climbingSession.copy(userId = user.id)
        val serviceResult = climbingSessionService.uploadSession(user.id, sessionWithUserId)
        if (!serviceResult.success) {
            return ResponseEntity.badRequest().body(mapOf("message" to serviceResult.message))
        }
        return ResponseEntity.ok(mapOf("message" to "Session uploaded successfully"))
    }
}