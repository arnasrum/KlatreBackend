package com.arnas.klatrebackend.controller

import com.arnas.klatrebackend.dataclass.AddGroupRequest
import com.arnas.klatrebackend.dataclass.GradingSystem
import com.arnas.klatrebackend.dataclass.GroupWithPlaces
import com.arnas.klatrebackend.dataclass.PlaceRequest
import com.arnas.klatrebackend.dataclass.User
import com.arnas.klatrebackend.service.GroupService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin(origins = arrayOf("http://localhost:5173"))
@RequestMapping("/api/groups")
class GroupController(
    private val groupService: GroupService,
) {

    @GetMapping("")
    open fun getGroups(user: User): ResponseEntity<Array<GroupWithPlaces>> {
        val serviceResult = groupService.getGroups(user.id)
        if (!serviceResult.success) {
            return ResponseEntity.badRequest().body(null)
        }else if(serviceResult.data == null) {
            return ResponseEntity.internalServerError().body(null)
        } else if(serviceResult.data.isEmpty()) {
            return ResponseEntity.ok(serviceResult.data)
        }
        return ResponseEntity(serviceResult.data, HttpStatus.ACCEPTED)
    }

    @PostMapping("")
    open fun addGroup(@RequestBody requestBody: AddGroupRequest, user: User): ResponseEntity<String> {
        requestBody.personal = false
        val serviceResult = groupService.addGroup(user.id, requestBody)
        if (!serviceResult.success) {
            return ResponseEntity.badRequest().body("Something went wrong when adding group")
        }
        return ResponseEntity.ok(serviceResult.message ?: "Group added successfully")
    }

    @PostMapping("/place")
    open fun addPlaceToGroup(
        user: User,
        @RequestParam groupID: Long,
        @RequestParam name: String,
        @RequestParam(required = false) description: String?
    ): ResponseEntity<String> {
        val placeRequest = PlaceRequest(group_id = groupID, name = name, description = description)
        groupService.addPlaceToGroup(groupID, placeRequest)
        return ResponseEntity.ok("Place added successfully")
    }

    @PutMapping("")
    open fun updateGroup(): ResponseEntity<String> {

        return ResponseEntity.ok("Group updated successfully")
    }

    @DeleteMapping("")
    open fun deleteGroup(user: User, @RequestBody requestBody: Map<String, String>): ResponseEntity<String> {
        val groupID = requestBody["groupID"]?.toLong() ?: return ResponseEntity.badRequest().body("GroupID is required")
        val serviceResult = groupService.deleteGroup(user.id, groupID)
        if(!serviceResult.success) {
            return ResponseEntity.internalServerError().body(serviceResult.message)
        }
        return ResponseEntity.ok("Group deleted successfully")
    }

    @GetMapping("/grading")
    open fun getGradingSystemsInGroup(@RequestParam groupID: Long, user: User): ResponseEntity<List<GradingSystem>> {
        val gradingSystems = groupService.getGradingSystemsInGroup(groupID)
        return ResponseEntity.ok(gradingSystems)
    }


}