package com.arnas.klatrebackend.features.invites

import com.arnas.klatrebackend.features.groups.Group
import com.arnas.klatrebackend.features.users.User

data class GroupInvite(
    val id: Long,
    val userId: Long,
    val senderId: Long,
    val groupId: Long,
    val status: String,
    val acceptedAt: Long?,
    val declinedAt: Long?,
    val revokedAt: Long?
)

data class GroupInviteDisplay(
    val id: Long,
    val group: Group,
    val sender: User,
    val status: String
)

