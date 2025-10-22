package com.arnas.klatrebackend.controllers

import com.arnas.klatrebackend.dataclasses.User
import com.arnas.klatrebackend.repositories.StatRepository
import com.arnas.klatrebackend.services.StatService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/stats")
class StatsController(
    private val statService: StatService,
) {


    @GetMapping("/group")
    fun getGroupActivity(groupId: Long, user: User) {
        val serviceResult = statService.getGroupActivityStats(groupId, "month")
        //if(serviceResult)
    }


}