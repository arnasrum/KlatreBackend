package com.arnas.klatrebackend.features.users

data class User(
    val id: Long,
    val email: String,
    val name: String
)

data class GroupUser(
    val id: Long,
    val name: String,
    val email: String,
    val isOwner: Boolean,
    val isAdmin: Boolean,
    val groupId: Long,
)