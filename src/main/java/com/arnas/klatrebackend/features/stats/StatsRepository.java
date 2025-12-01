package com.arnas.klatrebackend.features.stats;


import java.util.List;

public interface StatsRepository {

    List<UserGroupSessionStats> getUserAttemptActivity(long userId, long groupId);
}
