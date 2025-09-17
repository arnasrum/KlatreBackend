package com.arnas.klatrebackend.controllers

import com.arnas.klatrebackend.dataclasses.User
import com.arnas.klatrebackend.interfaces.services.GroupServiceInterface
import com.arnas.klatrebackend.interfaces.services.PlaceServiceInterface
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api/places")
@RestController
class PlaceController(
    private val groupService: GroupServiceInterface,
    private val placeService: PlaceServiceInterface,
) {


    @GetMapping("")
    fun getPlaces(@RequestParam groupID: Long, user: User): ResponseEntity<out Any> {
        val roleServiceResult = groupService.getGroupUserRole(user.id, groupID)
        if(!roleServiceResult.success) return ResponseEntity.badRequest().body(roleServiceResult.message)
        val result = placeService.getPlacesByGroupId(groupID, user.id)
        if(!result.success) return ResponseEntity.badRequest().body(result.message)
        return ResponseEntity.ok(result.data)
    }

}