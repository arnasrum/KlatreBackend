package com.arnas.klatrebackend.controllers

import com.arnas.klatrebackend.dataclasses.AddGroupRequest
import com.arnas.klatrebackend.dataclasses.GradingSystem
import com.arnas.klatrebackend.dataclasses.GroupWithPlaces
import com.arnas.klatrebackend.dataclasses.PlaceRequest
import com.arnas.klatrebackend.dataclasses.Role
import com.arnas.klatrebackend.dataclasses.UnauthorizedException
import com.arnas.klatrebackend.dataclasses.User
import com.arnas.klatrebackend.interfaces.services.GroupServiceInterface
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
@RequestMapping("/api/groups")
class GroupController(
    private val groupService: GroupServiceInterface,
) {

    @GetMapping("")
    fun getGroups(user: User): ResponseEntity<List<GroupWithPlaces>> {
        val groups = groupService.getGroups(user.id)
        return ResponseEntity(groups, HttpStatus.OK)
    }

    @PostMapping("")
    fun addGroup(@RequestBody requestBody: AddGroupRequest, user: User): ResponseEntity<String> {
        requestBody.personal = false
        groupService.addGroup(user.id, requestBody)
        return ResponseEntity.ok("Group added successfully")
    }

    @PostMapping("/place")
    fun addPlaceToGroup(
        user: User,
        @RequestParam groupID: Long,
        @RequestParam name: String,
        @RequestParam(required = false) description: String?
    ): ResponseEntity<String> {
        val placeRequest = PlaceRequest(groupId = groupID, name = name, description = description)
        groupService.addPlaceToGroup(user.id, groupID, placeRequest)
        return ResponseEntity.ok("Place added successfully")
    }

    @PutMapping("")
    fun updateGroup(): ResponseEntity<String> {

        return ResponseEntity.ok("Group updated successfully")
    }

    @DeleteMapping("")
    fun deleteGroup(user: User, @RequestBody requestBody: Map<String, String>): ResponseEntity<String> {
        val groupID = requestBody["groupID"]?.toLong() ?: return ResponseEntity.badRequest().body("GroupID is required")
        groupService.deleteGroup(user.id, groupID)
        return ResponseEntity.ok("Group deleted successfully")
    }

    @GetMapping("/grading")
    fun getGradingSystemsInGroup(@RequestParam groupID: Long, user: User): ResponseEntity<List<GradingSystem>> {
        val gradingSystems = groupService.getGradingSystemsInGroup(groupID)
        return ResponseEntity.ok(gradingSystems)
    }

    @PutMapping("/settings")
    fun updateSettings(): ResponseEntity<String> {
        return ResponseEntity.ok("Settings updated successfully")
    }


    @GetMapping("/uuid/{uuid}")
    fun getGroupById(@PathVariable uuid: String, user: User): ResponseEntity<Any> {
        val group = groupService.getGroupByUuid(uuid)
        return ResponseEntity.ok(mapOf("data" to group, "message" to "Group retried successfully"))
    }

    @GetMapping("/users")
    fun getUsersInGroup(@RequestParam("groupID") groupID: Long, user: User): ResponseEntity<Any> {
        val users = groupService.getUsersInGroup(user.id, groupID)
        return ResponseEntity.ok(users)
    }

    @PutMapping("/users/permissions")
    fun changeUserPermissions(
        @RequestParam(required = true) userID: Long,
        @RequestParam(required = true) groupID: Long,
        @RequestParam(required = true) role: Int,
        user: User
    ): ResponseEntity<Any> {
        try {
            groupService.changeGroupUserRole(user.id, userID,  role, groupID)
        } catch (e: UnauthorizedException) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mapOf("errorMessage" to e.message))
        } catch (e: RuntimeException) {
            return ResponseEntity.badRequest().body(mapOf("errorMessage" to e.message))
        }
        return ResponseEntity.ok().body( mapOf("success" to true, "message" to "User role updated successfully") )
    }

    @DeleteMapping("/users/kick")
    fun kickUserFromGroup(
        @RequestParam(required = true) userID: Long,
        @RequestParam(required = true) groupID: Long,
        user: User)
    : ResponseEntity<Any> {
        try {
            groupService.kickUserFromGroup(user.id, userID, groupID)
        } catch (e: UnauthorizedException) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mapOf("errorMessage" to e.message))
        } catch (e: RuntimeException) {
            return ResponseEntity.badRequest().body(mapOf("errorMessage" to e.message))
        }
        return ResponseEntity.ok().body( "message" to "User kicked successfully")
    }
}