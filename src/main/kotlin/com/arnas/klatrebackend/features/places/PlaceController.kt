package com.arnas.klatrebackend.features.places

import com.arnas.klatrebackend.features.users.User
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api/places")
@RestController
class PlaceController(
    private val placeService: PlaceServiceInterface,
) {


    @GetMapping("")
    fun getPlaces(@RequestParam groupId: Long, user: User): ResponseEntity<out Any> {
        val result = placeService.getPlacesByGroupId(groupId, user.id)
        return ResponseEntity.ok(result)
    }

    @PutMapping("")
    fun updatePlace(
        @RequestParam placeId: Long,
        @RequestParam(required = false) name: String?,
        @RequestParam(required = false) description: String?,
        @RequestParam(required = false) gradingSystemId: Long?,
        user: User
    ): ResponseEntity<out Any> {
        val updateObject = PlaceUpdateDTO(placeId, name, description, gradingSystemId)
        placeService.updatePlace(user.id, updateObject)
        return ResponseEntity.ok(mapOf("message" to "Place updated successfully"))
    }
}