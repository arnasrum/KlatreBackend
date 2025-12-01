package com.arnas.klatrebackend.features.groups

import com.arnas.klatrebackend.features.gradesystems.GradingSystem
import com.arnas.klatrebackend.features.places.PlaceRequest
import com.arnas.klatrebackend.util.exceptions.UnauthorizedException
import com.arnas.klatrebackend.features.users.User
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
    private val groupService: GroupService,
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
        @RequestParam groupId: Long,
        @RequestParam name: String,
        @RequestParam(required = false) description: String?
    ): ResponseEntity<String> {
        val placeRequest = PlaceRequest(groupId = groupId, name = name, description = description)
        groupService.addPlaceToGroup(user.id, groupId, placeRequest)
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
    fun getGradingSystemsInGroup(@RequestParam groupId: Long, user: User): ResponseEntity<List<GradingSystem>> {
        val gradingSystems = groupService.getGradingSystemsInGroup(groupId)
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
    fun getUsersInGroup(@RequestParam("groupID") groupId: Long, user: User): ResponseEntity<Any> {
        val users = groupService.getUsersInGroup(user.id, groupId)
        return ResponseEntity.ok(users)
    }

    @PutMapping("/users/permissions")
    fun changeUserPermissions(
        @RequestParam(required = true) userId: Long,
        @RequestParam(required = true) groupId: Long,
        @RequestParam(required = true) role: Int,
        user: User
    ): ResponseEntity<Any> {
        try {
            groupService.changeGroupUserRole(user.id, userId,  role, groupId)
        } catch (e: UnauthorizedException) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mapOf("errorMessage" to e.message))
        } catch (e: RuntimeException) {
            return ResponseEntity.badRequest().body(mapOf("errorMessage" to e.message))
        }
        return ResponseEntity.ok().body( mapOf("success" to true, "message" to "User role updated successfully") )
    }

    @DeleteMapping("/users/kick")
    fun kickUserFromGroup(
        @RequestParam(required = true) userId: Long,
        @RequestParam(required = true) groupId: Long,
        user: User
    )
    : ResponseEntity<Any> {
        try {
            groupService.kickUserFromGroup(user.id, userId, groupId)
        } catch (e: UnauthorizedException) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mapOf("errorMessage" to e.message))
        } catch (e: RuntimeException) {
            return ResponseEntity.badRequest().body(mapOf("errorMessage" to e.message))
        }
        return ResponseEntity.ok().body( "message" to "User kicked successfully")
    }
}