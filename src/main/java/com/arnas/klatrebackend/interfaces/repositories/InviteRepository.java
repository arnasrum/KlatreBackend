package com.arnas.klatrebackend.interfaces.repositories;

import com.arnas.klatrebackend.dataclasses.GroupInvite;

import java.util.List;

public interface InviteRepository {
    GroupInvite getGroupInviteById(long inviteId);
    long inviteUserToGroup(long userId, long groupId, long senderId);
    List<GroupInvite> getUserInvitesByStatus(long userId, String status);
    int acceptInvite(long inviteId);
    int declineInvite(long inviteId);
    int revokeInvite(long inviteId);


}
