package com.arnas.klatrebackend.controllers

import com.arnas.klatrebackend.dataclasses.RouteSendDTOUpdate
import com.arnas.klatrebackend.dataclasses.User
import com.arnas.klatrebackend.interfaces.services.RouteSendServiceInterface
import com.arnas.klatrebackend.services.RouteSendService
import jakarta.websocket.server.PathParam
import org.springframework.data.repository.query.Param
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api/routeSends")
@RestController
class RouteSendController(private val routeSendService: RouteSendServiceInterface) {


    @GetMapping("")
    fun getRouteSends(@RequestParam routeId: Long, user: User): ResponseEntity<out Any> {

        val result = routeSendService.getRouteSendByRoute(routeId, user.id)
        if(!result.success) return ResponseEntity.badRequest().body(mapOf("message" to result.message))

        return ResponseEntity.ok(listOf(result.data))
    }


    data class RouteSendUpdateRequest(
        val boulderID: Long,
        val attempts: Int?,
        val completed: Boolean?,
        val perceivedGrade: Long?
    )

    @PutMapping("/update")
    fun updateRouteSend(
        @RequestBody updateRequest: RouteSendUpdateRequest,
        user: User
    ): ResponseEntity<out Any> {

        val routeSendDTOUpdate = RouteSendDTOUpdate(
            user.id,
            updateRequest.boulderID,
            updateRequest.attempts,
            updateRequest.completed,
            updateRequest.perceivedGrade
        )
        println(routeSendDTOUpdate)
        val result = routeSendService.updateRouteSend(routeSendDTOUpdate)
        if(!result.success) return ResponseEntity.badRequest().body(mapOf("message" to result.message))
        return ResponseEntity.ok(mapOf("message" to result.message, "data" to listOf(result.data)))
    }



}