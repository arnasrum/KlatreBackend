package com.arnas.klatrebackend.controllers

import com.arnas.klatrebackend.dataclasses.ClimbingSessionDTO
import com.arnas.klatrebackend.dataclasses.RouteAttempt
import com.arnas.klatrebackend.dataclasses.RouteAttemptDTO
import com.arnas.klatrebackend.dataclasses.UpdateAttemptRequest
import com.arnas.klatrebackend.dataclasses.User
import com.arnas.klatrebackend.interfaces.services.ClimbingSessionService
import com.arnas.klatrebackend.services.AccessControlService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/climbingSessions")
class ClimbingSessionController(
    private val climbingSessionService: ClimbingSessionService,
    private val accessControlService: AccessControlService,
) {

    // this has to upload
    @PostMapping()
    fun uploadSession(@RequestBody climbingSession: ClimbingSessionDTO, user: User): ResponseEntity<out Any> {
        climbingSessionService.uploadSession(user.id, climbingSession)
        return ResponseEntity.ok(mapOf("message" to "Session uploaded successfully"))
    }


    @GetMapping("/active")
    fun getActiveSession(@RequestParam groupId: Long, user: User): ResponseEntity<out Any> {
        val activeSession = climbingSessionService.getActiveSessionByGroup(groupId, user.id)
        return ResponseEntity.ok().body(mapOf("message" to "Session opened successfully", "data" to activeSession))
    }

    @PostMapping("/open")
    fun openPersonalSession(@RequestParam groupId: Long, @RequestParam placeId: Long, user: User): ResponseEntity<out Any> {
        val openedSession = climbingSessionService.openSession(groupId, placeId, user.id)
        return ResponseEntity.ok().body(mapOf("message" to "Session opened successfully", "data" to openedSession))
    }

    @PutMapping("/close")
    fun closePersonalSession(@RequestParam sessionId: Long, @RequestParam save: Boolean, user: User): ResponseEntity<out Any> {
        climbingSessionService.closeSession(sessionId, save, user.id)
        return ResponseEntity.ok().body(mapOf("message" to "Session was closed successfully", "data" to null))
    }

    @GetMapping("/attempts")
    fun getSessionAttempts(@RequestParam sessionId: Long, user: User): ResponseEntity<out Any> {
        val routeAttempts = climbingSessionService.getRouteAttempts(sessionId)
        return ResponseEntity.ok(mapOf("data" to routeAttempts))
    }

    @PostMapping("/add/attempt")
    fun addAttempt(@RequestParam sessionId: Long, @RequestBody routeAttempt: RouteAttemptDTO, user: User): ResponseEntity<out Any> {
        climbingSessionService.addRouteAttempt(sessionId, user.id,  routeAttempt)
        return ResponseEntity.ok(mapOf("message" to "Attempt added successfully", "data" to null))
    }

    @PutMapping("/update/attempt")
    fun updateAttempt(@RequestBody newRouteAttempt: RouteAttempt, user: User): ResponseEntity<out Any> {
        climbingSessionService.updateRouteAttempt(user.id, newRouteAttempt)
        return ResponseEntity.ok(mapOf("message" to "updated successfully", "data" to null))
    }

    @DeleteMapping("/remove/attempt")
    fun addAttempt(@RequestBody requestBody: Map<String, String>, user: User): ResponseEntity<out Any> {
        val routeAttemptId =  requestBody["attemptId"]?.toLong() ?: return ResponseEntity.badRequest().body(mapOf("message" to "Route attempt id is required"))
        climbingSessionService.removeRouteAttempt(routeAttemptId, user.id)
        return ResponseEntity.ok(mapOf("message" to "Attempt removed successfully", "data" to null))
    }

    @GetMapping("/past/{groupId}")
    fun getPastSessions(@PathVariable groupId: Long, user: User): ResponseEntity<out Any> {
        if(!accessControlService.hasGroupAccess(user.id, groupId))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not a member of group")
        val serviceResult = climbingSessionService.getPastSessions(groupId, user.id)
        return ResponseEntity.ok(mapOf("data" to serviceResult))
    }
}