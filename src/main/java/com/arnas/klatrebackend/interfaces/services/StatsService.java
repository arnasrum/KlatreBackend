package com.arnas.klatrebackend.interfaces.services;

import com.arnas.klatrebackend.dataclasses.UserGroupSessionStats;

import java.util.List;

public interface StatsService {

    List<UserGroupSessionStats> getUserAttemptActivity(long userId, long groupId);
}
