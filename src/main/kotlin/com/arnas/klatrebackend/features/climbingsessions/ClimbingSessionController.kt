package com.arnas.klatrebackend.features.climbingsessions

import com.arnas.klatrebackend.features.users.User
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/climbingSessions")
class ClimbingSessionController(
    private val climbingSessionService: ClimbingSessionService
) {

    @PostMapping("")
    fun uploadSession(@RequestBody climbingSession: ClimbingSessionDTO, user: User): ResponseEntity<String> {
        climbingSessionService.uploadSession(user.id, climbingSession)
        return ResponseEntity.ok("Session uploaded successfully")
    }

    @GetMapping("/active")
    fun getActiveSession(@RequestParam groupId: Long, user: User): ResponseEntity<ClimbingSession?> {
        val activeSession = climbingSessionService.getActiveSessionByGroup(groupId, user.id)
        return ResponseEntity.ok().body(activeSession)
    }

    @PostMapping("/open")
    fun openPersonalSession(@RequestParam groupId: Long, @RequestParam placeId: Long, user: User): ResponseEntity<ClimbingSession> {
        val openedSession = climbingSessionService.openSession(groupId, placeId, user.id)
        return ResponseEntity.ok().body(openedSession)
    }

    @PutMapping("/close")
    fun closePersonalSession(@RequestParam sessionId: Long, @RequestParam save: Boolean, user: User): ResponseEntity<String> {
        climbingSessionService.closeSession(sessionId, save, user.id)
        return ResponseEntity.ok().body("Session was closed successfully")
    }

    @GetMapping("/attempts")
    fun getSessionAttempts(@RequestParam sessionId: Long, user: User): ResponseEntity<List<RouteAttemptDisplay>> {
        val routeAttempts = climbingSessionService.getRouteAttempts(sessionId)
        return ResponseEntity.ok().body(routeAttempts)
    }

    @PostMapping("/add/attempt")
    fun addAttempt(@RequestParam sessionId: Long, @RequestBody routeAttempt: RouteAttemptDTO, user: User): ResponseEntity<String> {
        climbingSessionService.addRouteAttempt(sessionId, user.id, routeAttempt)
        return ResponseEntity.ok("Attempt added successfully")
    }

    @PutMapping("/update/attempt")
    fun updateAttempt(@RequestBody newRouteAttempt: RouteAttempt, user: User): ResponseEntity<String> {
        climbingSessionService.updateRouteAttempt(user.id, newRouteAttempt)
        return ResponseEntity.ok("updated successfully")
    }

    @DeleteMapping("/remove/attempt/{attemptId}")
    fun removeAttempt(@PathVariable attemptId: Long, user: User): ResponseEntity<String> {
        val attempt = climbingSessionService.getRouteAttemptById(attemptId)
        climbingSessionService.removeRouteAttempt(attemptId, attempt.session, user.id)
        return ResponseEntity.ok("Attempt removed successfully")
    }

    @GetMapping("/past/{groupId}")
    fun getPastSessions(@PathVariable groupId: Long, user: User): ResponseEntity<List<ClimbingSessionDisplay>> {
        val serviceResult = climbingSessionService.getPastSessions(groupId, user.id)
        return ResponseEntity.ok().body(serviceResult)
    }
}

