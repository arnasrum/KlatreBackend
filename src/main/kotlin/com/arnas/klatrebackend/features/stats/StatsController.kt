package com.arnas.klatrebackend.features.stats

import com.arnas.klatrebackend.features.users.User
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/stats")
class StatsController(
    private val statsService: StatsService
) {

    @RequestMapping("/user")
    fun getStats(@RequestParam groupId: Long, user: User): ResponseEntity<List<UserGroupSessionStats>> {
        val userSessionStats = statsService.getUserAttemptActivity(user.id, groupId)
        return ResponseEntity.ok().body(userSessionStats)
    }
}

