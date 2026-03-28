package com.arnas.klatrebackend.features.stats

import com.arnas.klatrebackend.features.auth.RequireGroupAccess
import org.springframework.stereotype.Service

@Service
class StatsServiceDefault(
    private val statsRepository: StatsRepository
) : StatsService {

    @RequireGroupAccess
    override fun getUserAttemptActivity(userId: Long, groupId: Long): List<UserGroupSessionStats> {
        return statsRepository.getUserAttemptActivity(userId, groupId)
    }
}

