package com.arnas.klatrebackend.services

import com.arnas.klatrebackend.dataclasses.GroupRouteStats
import com.arnas.klatrebackend.dataclasses.UserRouteStats
import com.arnas.klatrebackend.repositories.StatRepository
import org.springframework.stereotype.Service

@Service
class StatService(
    private val statRepository: StatRepository
) {


    fun getRouteStats(routeId: Long) {

    }

    fun getUserRouteStats(groupId: Long, userId: Long) {
        val totalAttempts = statRepository.getUserTotalAttempts(userId, groupId)?: 0
        val hardestRouteId = statRepository.getUserHardestSend(userId, groupId)?: 0
        val totalCompleted = statRepository.getUserTotalCompletedRoutes(userId, groupId)?: 0

        val userStats = UserRouteStats(userId, hardestRouteId, totalAttempts, totalCompleted)
    }

    fun getGroupRouteStats(groupId: Long) {

        val totalAttempts = statRepository.getGroupTotalAttempts(groupId)?: 0
        val totalCompleted = statRepository.getGroupTotalCompletedRoutes(groupId)?: 0
        val hardestRouteSend = statRepository.getGroupHardestRouteCompleted(groupId)?: 0
        val groupStats = GroupRouteStats(groupId, totalAttempts, totalCompleted, hardestRouteSend)
    }

    fun getGroupActivityStats(groupId: Long, timeAggregate: String) {
        statRepository.groupActivityOverTime(groupId, timeAggregate)
    }







}