package com.arnas.klatrebackend.controllers

import com.arnas.klatrebackend.dataclasses.RouteDTO
import com.arnas.klatrebackend.dataclasses.User
import com.arnas.klatrebackend.interfaces.services.RouteServiceInterface
import com.arnas.klatrebackend.services.AccessControlService
import com.arnas.klatrebackend.services.PlaceService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@Tag(name = "Route", description = "Route CRUD operations")
@RequestMapping("/boulders")
class RouteController(
    private val boulderService: RouteServiceInterface,
    private val placeService: PlaceService,
    private val accessControlService: AccessControlService,
) {

    @GetMapping("/place")
    fun getRoutesByPlace(
        @RequestParam placeId: Long,
        @RequestParam page: Int,
        @RequestParam limit: Int,
        user: User
    ): ResponseEntity<out Any> {

        if(!userHasPermissionToPlace(user.id, placeId)) {
            return ResponseEntity(
                mapOf(
                "message" to "User does not have sufficient permissions"),
                HttpStatus.UNAUTHORIZED
            )
        }
        val pagedBoulders = boulderService.getRoutesByPlace(placeId, page, limit)
        return ResponseEntity.ok().body(pagedBoulders)

    }

    @PostMapping("/place/add")
    fun addRoutesToPlace(
        @RequestParam placeID: Long,
        @RequestParam name: String,
        @RequestParam grade: Long,
        @RequestParam(required = false) description: String?,
        @RequestParam(required = false) image: MultipartFile?,
        user: User
    ): ResponseEntity<out Any> {
        boulderService.addRoute(user.id, RouteDTO(name, grade, placeID, description, true, image))
        return ResponseEntity(HttpStatus.OK)
    }

    @PutMapping("/update/{routeId}")
    fun putRoute(
        @PathVariable routeId: Long,
        @RequestParam(required = false) name: String?,
        @RequestParam(required = false) grade: String?,
        @RequestParam(required = false) description: String?,
        @RequestParam(required = false) active: Boolean?,
        @RequestParam(required = false) image: MultipartFile?,
        user: User
    ): ResponseEntity<Map<String, Any>> {
        val userID: Long = user.id

        val requestBody = mutableMapOf<String, String>().apply {
            name?.let { put("name", it) }
            grade?.let { put("grade", it) }
            description?.let { put("description", it) }
            active?.let { put("active", it.toString()) }
        }
        boulderService.updateRoute(routeId, userID, requestBody, image)
        return ResponseEntity.ok(mapOf("message" to "Boulder updated successfully"))
    }

    private fun userHasPermissionToPlace(userId: Long, placeId: Long): Boolean {
        val place = placeService.getPlaceById(placeId)
            ?: throw RuntimeException("Place not found")
        return accessControlService.hasGroupAccess(userId, place.groupId)
    }
}