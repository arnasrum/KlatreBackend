package com.arnas.klatrebackend.features.invites;

import java.util.List;

public interface InviteService {

    void sendInvite(long userId, long groupId, long invitedUserId);
    List<GroupInviteDisplay> getUserPendingInvites(long userId);
    void acceptInvite(long inviteId, long userId);
    void rejectInvite(long inviteId, long userId);
}