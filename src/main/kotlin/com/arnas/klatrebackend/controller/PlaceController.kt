package com.arnas.klatrebackend.controller

import com.arnas.klatrebackend.dataclass.PlaceWithBoulders
import com.arnas.klatrebackend.dataclass.User
import com.arnas.klatrebackend.repository.GroupRepository
import com.arnas.klatrebackend.service.BoulderService
import com.arnas.klatrebackend.service.GroupService
import com.arnas.klatrebackend.service.PlaceService
import com.arnas.klatrebackend.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@CrossOrigin(origins = arrayOf("http://localhost:5173"))
@RequestMapping("/api/places")
@RestController
class PlaceController(
    private val userService: UserService,
    private val groupService: GroupService,
    private val groupRepository: GroupRepository,
    private val boulderService: BoulderService,
    private val placeService: PlaceService,
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