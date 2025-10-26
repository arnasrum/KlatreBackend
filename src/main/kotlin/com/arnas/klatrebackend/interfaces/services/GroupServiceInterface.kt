package com.arnas.klatrebackend.interfaces.services

import com.arnas.klatrebackend.dataclasses.AddGroupRequest
import com.arnas.klatrebackend.dataclasses.GradingSystem
import com.arnas.klatrebackend.dataclasses.Group
import com.arnas.klatrebackend.dataclasses.GroupUser
import com.arnas.klatrebackend.dataclasses.GroupWithPlaces
import com.arnas.klatrebackend.dataclasses.PlaceRequest

interface GroupServiceInterface {
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