package com.arnas.klatrebackend.features.invites

interface InviteRepository {
    fun getGroupInviteById(inviteId: Long): GroupInvite
    fun inviteUserToGroup(userId: Long, groupId: Long, senderId: Long): Long
    fun getUserInvitesByStatus(userId: Long, status: String): List<GroupInvite>
    fun acceptInvite(inviteId: Long): Int
    fun declineInvite(inviteId: Long): Int
    fun revokeInvite(inviteId: Long): Int
}

