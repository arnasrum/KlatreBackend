package com.arnas.klatrebackend.controller

import com.arnas.klatrebackend.dataclass.Group
import com.arnas.klatrebackend.dataclass.GroupWithPlaces
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
@CrossOrigin(origins = ["http://localhost:5173"])
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
    open fun addGroup(@RequestParam accessToken: String, @RequestBody requestBody: Map<String, String>): ResponseEntity<String> {
        val userID: Int = userService.getUserID(accessToken) ?: return  ResponseEntity.badRequest().body("Invalid access token")
        val serviceResult = groupService.addGroup(userID.toLong(), requestBody)
        if (!serviceResult.success) {
            return ResponseEntity.badRequest().body("Something went wrong when adding group")
        }
        return ResponseEntity.ok(serviceResult.message ?: "Group added successfully")
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