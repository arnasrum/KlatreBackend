package com.arnas.klatrebackend.services;

import com.arnas.klatrebackend.annotation.RequireGroupAccess;
import com.arnas.klatrebackend.dataclasses.UserGroupSessionStats;
import com.arnas.klatrebackend.interfaces.repositories.StatsRepository;
import com.arnas.klatrebackend.interfaces.services.StatsService;
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
