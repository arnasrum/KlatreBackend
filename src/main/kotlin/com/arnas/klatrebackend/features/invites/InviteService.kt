package com.arnas.klatrebackend.features.invites

interface InviteService {
    fun sendInvite(userId: Long, groupId: Long, invitedUserId: Long)
    fun getUserPendingInvites(userId: Long): List<GroupInviteDisplay>
    fun acceptInvite(inviteId: Long, userId: Long)
    fun rejectInvite(inviteId: Long, userId: Long)
}

