package com.arnas.klatrebackend.controller

import com.arnas.klatrebackend.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin(origins = arrayOf("http://localhost:5173"))
class GroupController(
    private val userService: UserService,
) {


    @GetMapping("/groups")
    open fun getGroups(@RequestParam accessToken: String): ResponseEntity<String> {

        return ResponseEntity.ok("Group retrieved successfully")
    }

    @PostMapping("/groups")
    open fun addGroup(): ResponseEntity<String> {

        return ResponseEntity.ok("Group added successfully")
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