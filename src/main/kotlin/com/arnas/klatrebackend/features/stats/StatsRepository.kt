package com.arnas.klatrebackend.features.stats

interface StatsRepository {
    fun getUserAttemptActivity(userId: Long, groupId: Long): List<UserGroupSessionStats>
}

