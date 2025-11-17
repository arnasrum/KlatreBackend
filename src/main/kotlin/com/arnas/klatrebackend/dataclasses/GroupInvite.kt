package com.arnas.klatrebackend.dataclasses

data class GroupInvite(
    val id: Long,
    val userId: Long,
    val senderId: Long,
    val groupId: Long,
    val status: String,
    val acceptedAt: Long? = null,
    val declinedAt: Long? = null,
    val revokedAt: Long? = null,
)

data class GroupInviteDisplay(
    val id: Long,
    val group: Group,
    val sender: User,
    val status: String,
)
