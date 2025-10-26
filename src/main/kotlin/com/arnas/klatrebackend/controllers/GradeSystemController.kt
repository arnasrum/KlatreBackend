package com.arnas.klatrebackend.controllers

import com.arnas.klatrebackend.dataclasses.User
import com.arnas.klatrebackend.interfaces.services.GradeSystemServiceInterface
import com.nimbusds.jose.shaded.gson.Gson
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/gradingSystems")
class GradeSystemController(private val gradeSystemService: GradeSystemServiceInterface) {

    data class GradeEntry(
        val name: String,
        val from: String,
        val to: String
    )

    @PostMapping("/grades")
    fun createGradeSystem(
        @RequestParam referenceGradeSystemID: Long,
        @RequestParam("newGradeSystemName") name: String,
        @RequestParam("grades") grades: String,
        @RequestParam("groupId") groupId: Long,
        user: User
    ): ResponseEntity<Any> {
        val parsedGrades = Gson().fromJson(grades, Array<GradeEntry>::class.java)
        val newGrades = parsedGrades.map { mapOf("name" to it.name, "from" to it.from, "to" to it.to) }
        gradeSystemService.makeGradingSystem(groupId, user.id, referenceGradeSystemID, name, newGrades)
        return ResponseEntity.ok().body("message" to "Grade system created successfully")
    }

    @DeleteMapping("")
    fun deleteGradeSystem(@RequestParam gradingSystemId: Long, @RequestParam groupId: Long, user: User): ResponseEntity<Any> {
        val result = gradeSystemService.deleteGradeSystem(gradingSystemId, groupId, user.id)
        return ResponseEntity.ok("message" to "Grade system deleted successfully")
    }
        
}