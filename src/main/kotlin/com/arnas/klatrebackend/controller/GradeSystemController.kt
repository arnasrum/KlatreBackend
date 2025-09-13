package com.arnas.klatrebackend.controller

import com.arnas.klatrebackend.dataclass.User
import com.arnas.klatrebackend.service.GradeSystemService
import com.nimbusds.jose.shaded.gson.Gson
import org.springframework.boot.json.JsonParser
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin(origins = arrayOf("http://localhost:5173"))
@RequestMapping("/api/gradesystems")
class GradeSystemController(private val gradeSystemService: GradeSystemService) {

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
        val result = gradeSystemService.makeGradingSystem(groupId, referenceGradeSystemID, name, newGrades)
        if(!result.success) return ResponseEntity.badRequest().body(result.message)
        return ResponseEntity.ok().body(result.message)
    }
        
}