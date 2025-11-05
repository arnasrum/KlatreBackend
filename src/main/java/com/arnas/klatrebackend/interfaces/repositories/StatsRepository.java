package com.arnas.klatrebackend.interfaces.repositories;

import com.arnas.klatrebackend.dataclasses.UserGroupSessionStats;

import java.util.List;

public interface StatsRepository {

    List<UserGroupSessionStats> getUserAttemptActivity(long userId, long groupId);
}
