package com.arnas.klatrebackend.features.stats

interface StatsService {
    fun getUserAttemptActivity(userId: Long, groupId: Long): List<UserGroupSessionStats>
}

