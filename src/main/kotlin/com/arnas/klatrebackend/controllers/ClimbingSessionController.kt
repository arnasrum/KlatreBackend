package com.arnas.klatrebackend.controllers

import com.arnas.klatrebackend.dataclasses.ClimbingSessionDTO
import com.arnas.klatrebackend.dataclasses.RouteAttemptDTO
import com.arnas.klatrebackend.dataclasses.UpdateAttemptRequest
import com.arnas.klatrebackend.dataclasses.User
import com.arnas.klatrebackend.interfaces.services.ClimbingSessionServiceInterface
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
    private val climbingSessionService: ClimbingSessionServiceInterface,
    private val accessControlService: AccessControlService,
) {

    @GetMapping()
    fun getAllSessions(@RequestParam groupId: Long, user: User): ResponseEntity<out Any> {
        val serviceResult = climbingSessionService.getSessionsByGroup(groupId, user.id)
        if(!serviceResult.success) return ResponseEntity.badRequest().body(mapOf("message" to serviceResult.message))
        return ResponseEntity.ok(mapOf("data" to serviceResult.data))
    }

    // this has to upload
    @PostMapping()
    fun uploadSession(@RequestBody climbingSession: ClimbingSessionDTO, user: User): ResponseEntity<out Any> {
        val serviceResult = climbingSessionService.uploadSession(user.id, climbingSession)
        if (!serviceResult.success) {
            return ResponseEntity.badRequest().body(mapOf("message" to serviceResult.message))
        }
        return ResponseEntity.ok(mapOf("message" to "Session uploaded successfully"))
    }


    @GetMapping("/active")
    fun getActiveSession(@RequestParam groupId: Long, user: User): ResponseEntity<out Any> {
        val serviceResult = climbingSessionService.getSessionsByGroup(groupId, user.id)
        if(!serviceResult.success) return ResponseEntity.badRequest().body(mapOf("message" to serviceResult.message))
        return ResponseEntity.ok().body(mapOf("message" to "Session opened successfully", "data" to serviceResult.data))
    }

    @PostMapping("/open")
    fun openPersonalSession(@RequestParam groupId: Long, @RequestParam placeId: Long, user: User): ResponseEntity<out Any> {
        val serviceResult = climbingSessionService.openSession(groupId, placeId, user.id)
        return ResponseEntity.ok().body(mapOf("message" to "Session opened successfully", "data" to serviceResult.data))
    }

    @PutMapping("/close")
    fun closePersonalSession(@RequestParam sessionId: Long, @RequestParam save: Boolean, user: User): ResponseEntity<out Any> {
        val serviceResult = climbingSessionService.closeSession(sessionId, save, user.id)
        if(serviceResult.success) return ResponseEntity.ok().body(mapOf("message" to "Session closed successfully"))
        return ResponseEntity.ok().body(mapOf("message" to serviceResult.message, "data" to serviceResult.data))
    }

    @GetMapping("/attempts")
    fun getSessionAttempts(@RequestParam sessionId: Long, user: User): ResponseEntity<out Any> {
        val serviceResult = climbingSessionService.getRouteAttempts(sessionId)
        if(!serviceResult.success) return ResponseEntity.badRequest().body(mapOf("message" to serviceResult.message))
        return ResponseEntity.ok(mapOf("data" to serviceResult.data))
    }

    @PostMapping("/add/attempt")
    fun addAttempt(@RequestParam sessionId: Long, @RequestBody routeAttempt: RouteAttemptDTO, user: User): ResponseEntity<out Any> {
        val serviceResult = climbingSessionService.addRouteAttempt(sessionId, user.id,  routeAttempt)
        if(!serviceResult.success) return ResponseEntity.badRequest().body(mapOf("message" to serviceResult.message))
        return ResponseEntity.ok(mapOf("message" to "Attempt added successfully", "data" to serviceResult.data))
    }

    @PutMapping("/update/attempt")
    fun updateAttempt(@RequestBody requestBody: UpdateAttemptRequest, user: User): ResponseEntity<out Any> {
        val serviceResult = climbingSessionService.updateRouteAttempt(user.id, requestBody)
        if(!serviceResult.success) return ResponseEntity.badRequest().body(mapOf("message" to serviceResult.message))
        return ResponseEntity.ok(mapOf("message" to serviceResult.message, "data" to serviceResult.data))
    }

    @DeleteMapping("/remove/attempt")
    fun addAttempt(@RequestBody requestBody: Map<String, String>, user: User): ResponseEntity<out Any> {
        val routeAttemptId =  requestBody["attemptId"]?.toLong() ?: return ResponseEntity.badRequest().body(mapOf("message" to "Route attempt id is required"))
        val serviceResult = climbingSessionService.removeRouteAttempt(routeAttemptId, user.id)
        return ResponseEntity.ok(mapOf("message" to "Attempt removed successfully", "data" to serviceResult.data))
    }

    @GetMapping("/past/{groupId}")
    fun getPastSessions(@PathVariable groupId: Long, user: User): ResponseEntity<out Any> {
        if(!accessControlService.hasGroupAccess(user.id, groupId))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not a member of group")
        val serviceResult = climbingSessionService.getPastSessions(groupId, user.id)
        return ResponseEntity.ok(mapOf("data" to serviceResult))
    }
}