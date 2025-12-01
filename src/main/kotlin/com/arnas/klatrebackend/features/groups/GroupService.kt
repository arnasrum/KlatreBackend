package com.arnas.klatrebackend.features.groups

import com.arnas.klatrebackend.features.gradesystems.GradingSystem
import com.arnas.klatrebackend.features.users.GroupUser
import com.arnas.klatrebackend.features.places.PlaceRequest

interface GroupService {
    fun getGroups(userId: Long): List<GroupWithPlaces>
    fun addGroup(userId: Long, request: AddGroupRequest): Long
    fun addPlaceToGroup(userId: Long, groupId: Long, placeRequest: PlaceRequest): Long
    fun deleteGroup(userId: Long, groupId: Long)
    fun getGroupUserRole(userId: Long, groupId: Long): Int?
    fun changeGroupUserRole(userId: Long, targetUserId: Long, newRoleId: Int, groupId: Long)
    fun kickUserFromGroup(userId: Long, targetUserId: Long, groupId: Long)
    fun getGradingSystemsInGroup(groupId: Long): List<GradingSystem>
    fun getUsersInGroup(userId: Long, groupId: Long): List<GroupUser>
    fun getGroupByUuid(groupUuid: String): Group
}