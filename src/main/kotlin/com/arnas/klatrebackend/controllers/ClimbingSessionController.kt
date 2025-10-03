package com.arnas.klatrebackend.controllers

import com.arnas.klatrebackend.dataclasses.ClimbingSession
import com.arnas.klatrebackend.dataclasses.User
import com.arnas.klatrebackend.services.ClimbingSessionService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/sessions")
class ClimbingSessionController(
    private val climbingSessionService: ClimbingSessionService,
) {

    @GetMapping("/")
    fun getPastSessions(@RequestParam groupId: Long, user: User): ResponseEntity<out Any> {


        return ResponseEntity.ok().body(mapOf("message" to "Sessions fetched successfully"))
    }


    @PostMapping("/upload")
    fun uploadSession(@RequestParam session: ClimbingSession, user: User): ResponseEntity<out Any> {

        session.routeSends.isEmpty().let {
            if (it) return ResponseEntity.badRequest().body(mapOf("message" to "Session must have at least one attmept"))
        }
        val serviceResult = climbingSessionService.uploadSession(user.id, session)
        if (!serviceResult.success) {
            return ResponseEntity.badRequest().body(mapOf("message" to serviceResult.message))
        }
        return ResponseEntity.ok(mapOf("message" to "Session uploaded successfully"))
    }
}