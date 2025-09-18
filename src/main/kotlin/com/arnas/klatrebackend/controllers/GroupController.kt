package com.arnas.klatrebackend.controllers

import com.arnas.klatrebackend.dataclasses.AddGroupRequest
import com.arnas.klatrebackend.dataclasses.GradingSystem
import com.arnas.klatrebackend.dataclasses.GroupWithPlaces
import com.arnas.klatrebackend.dataclasses.PlaceRequest
import com.arnas.klatrebackend.dataclasses.Role
import com.arnas.klatrebackend.dataclasses.User
import com.arnas.klatrebackend.interfaces.services.GroupServiceInterface
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
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
    fun addGroup(@RequestBody requestBody: AddGroupRequest, user: User): ResponseEntity<String> {
        requestBody.personal = false
        val serviceResult = groupService.addGroup(user.id, requestBody)
        if (!serviceResult.success) {
            return ResponseEntity.badRequest().body("Something went wrong when adding group")
        }
        return ResponseEntity.ok(serviceResult.message ?: "Group added successfully")
    }

    @PostMapping("/place")
    fun addPlaceToGroup(
        user: User,
        @RequestParam groupID: Long,
        @RequestParam name: String,
        @RequestParam(required = false) description: String?
    ): ResponseEntity<String> {
        val placeRequest = PlaceRequest(groupId = groupID, name = name, description = description)
        groupService.addPlaceToGroup(groupID, placeRequest)
        return ResponseEntity.ok("Place added successfully")
    }

    @PutMapping("")
    fun updateGroup(): ResponseEntity<String> {

        return ResponseEntity.ok("Group updated successfully")
    }

    @DeleteMapping("")
    fun deleteGroup(user: User, @RequestBody requestBody: Map<String, String>): ResponseEntity<String> {
        val groupID = requestBody["groupID"]?.toLong() ?: return ResponseEntity.badRequest().body("GroupID is required")
        val serviceResult = groupService.deleteGroup(user.id, groupID)
        if(!serviceResult.success) {
            return ResponseEntity.internalServerError().body(serviceResult.message)
        }
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

    @GetMapping("/users")
    fun getUsersInGroup(@RequestParam("groupID") groupID: Long, user: User): ResponseEntity<Any> {
        if(groupService.getGroupUserRole(user.id, groupID).data == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not a member of group")
        val result = groupService.getUsersInGroup(groupID)
        if(!result.success) return ResponseEntity.badRequest().body(result.message)
        return ResponseEntity.ok(result.data)
    }

    @PutMapping("/users/permissions")
    fun changeUserPermissions(@RequestParam(required = true) userID: Long,
                              @RequestParam(required = true) groupID: Long,
                              @RequestParam(required = true) role: Int,
                              user: User): ResponseEntity<Any>
    {
        val userRole = groupService.getGroupUserRole(user.id, groupID).data?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mapOf("errorMessage" to "You are not a member of this group"))
        if(!(userRole == Role.ADMIN.id || userRole == Role.OWNER.id)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mapOf("errorMessage" to "User is not an admin of group"))
        val originalUserRole = groupService.getGroupUserRole(userID, groupID).data?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body({"errorMessage" to "User is not a member of group"})
        if(userRole >= originalUserRole) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("errorMessage" to "User has already the same or higher role"))
        groupService.changeGroupUserRole(userID, role, groupID)
        return ResponseEntity.ok().body( mapOf("success" to true, "message" to "User role updated successfully") )
    }

    @DeleteMapping("/users/kick")
    fun kickUserFromGroup(@RequestParam(required = true) userID: Long,
                          @RequestParam(required = true) groupID: Long,
                          user: User): ResponseEntity<Any> {

        val userRole = groupService.getGroupUserRole(user.id, groupID).data?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mapOf("errorMessage" to "You are not a member of this group"))
        if(!(userRole == Role.ADMIN.id || userRole == Role.OWNER.id)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mapOf("errorMessage" to "User is not an admin of group"))
        val originalUserRole = groupService.getGroupUserRole(userID, groupID).data?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body({"errorMessage" to "User is not a member of group"})
        if(userRole >= originalUserRole) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("errorMessage" to "User has already the same or higher role"))
        val result = groupService.removeUserFromGroup(userID, groupID)
        if(!result.success) return ResponseEntity.badRequest().body(result.message)
        return ResponseEntity.ok().body( mapOf("success" to true, "message" to result.message) )
    }
}