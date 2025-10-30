package com.arnas.klatrebackend.interfaces.repositories;

import com.arnas.klatrebackend.dataclasses.AddGroupRequest;
import com.arnas.klatrebackend.dataclasses.Group;
import com.arnas.klatrebackend.dataclasses.GroupUser;
import jakarta.annotation.Nullable;

public interface GroupRepository {
    Group[] getGroups(Long userID);
    long addGroup(AddGroupRequest group);
    int deleteGroup(long groupId);
    GroupUser[] getGroupUsers(long groupId);
    int addUserToGroup(long userId, long groupId, int role);
    @Nullable Integer getUserGroupRole(long userId, long groupId);
    int updateUserGroupRole(long userId, long groupId, int newRoleId);
    int deleteUserFromGroup(long userId, long groupId);
    @Nullable Group getGroupById(long id);
    @Nullable Group getGroupByUuid(String groupUuid);
}
