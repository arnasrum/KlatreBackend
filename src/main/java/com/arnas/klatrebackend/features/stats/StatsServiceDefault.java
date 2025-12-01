package com.arnas.klatrebackend.features.stats;

import com.arnas.klatrebackend.features.auth.RequireGroupAccess;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatsServiceDefault implements StatsService {

    final private StatsRepository statsRepository;

    public StatsServiceDefault(StatsRepository statsRepository) {
        this.statsRepository = statsRepository;
    }

    @RequireGroupAccess
    public List<UserGroupSessionStats> getUserAttemptActivity(long userId, long groupId) {
        return statsRepository.getUserAttemptActivity(userId, groupId);
    }
}
