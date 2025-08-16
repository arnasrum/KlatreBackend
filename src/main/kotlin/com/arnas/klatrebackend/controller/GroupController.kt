package com.arnas.klatrebackend.controller

import com.arnas.klatrebackend.dataclass.AddGroupRequest
import com.arnas.klatrebackend.dataclass.GroupWithPlaces
import com.arnas.klatrebackend.dataclass.PlaceRequest
import com.arnas.klatrebackend.service.GroupService
import com.arnas.klatrebackend.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin(origins = arrayOf("http://localhost:5173"))
class GroupController(
    private val userService: UserService,
    private val groupService: GroupService,
) {


    @GetMapping("/groups")
    open fun getGroups(@RequestParam accessToken: String): ResponseEntity<Array<GroupWithPlaces>> {
        if(accessToken.isEmpty()) {
            return ResponseEntity.badRequest().body(null)
        }
        val serviceResult = groupService.getGroups(userService.getUserID(accessToken)!!.toLong())
        if (!serviceResult.success) {
            return ResponseEntity.badRequest().body(null)
        }else if(serviceResult.data == null) {
            return ResponseEntity.internalServerError().body(null)
        } else if(serviceResult.data.isEmpty()) {
            return ResponseEntity.ok(serviceResult.data)
        }
        return ResponseEntity(serviceResult.data, HttpStatus.ACCEPTED)
    }

    @PostMapping("/groups")
    open fun addGroup(@RequestParam accessToken: String, @RequestBody requestBody: AddGroupRequest): ResponseEntity<String> {
        requestBody.personal = false
        val userID: Long = userService.getUserID(accessToken)
        val serviceResult = groupService.addGroup(userID.toLong(), requestBody)
        if (!serviceResult.success) {
            return ResponseEntity.badRequest().body("Something went wrong when adding group")
        }
        return ResponseEntity.ok(serviceResult.message ?: "Group added successfully")
    }

    @PostMapping("/groups/place")
    open fun addPlaceToGroup(@RequestParam accessToken: String,
                             @RequestParam groupID: Long,
                             @RequestBody requestBody: Map<String, String>): ResponseEntity<String> {

        // Check if user has permissions to add place to group

        if(requestBody["name"] == null) {
            return ResponseEntity.badRequest().body("Name is required")
        }
        val placeRequest = PlaceRequest(group_id = groupID, name = requestBody["name"]!!, description = requestBody.getOrDefault("description", null))
        groupService.addPlaceToGroup(groupID, placeRequest)

        return ResponseEntity.ok("Place added successfully")
    }



    @PutMapping("/groups")
    open fun updateGroup(): ResponseEntity<String> {

        return ResponseEntity.ok("Group updated successfully")
    }

    @DeleteMapping("/groups")
    open fun deleteGroup(): ResponseEntity<String> {

        return ResponseEntity.ok("Group deleted successfully")
    }
}