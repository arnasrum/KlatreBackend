package com.arnas.klatrebackend.interfaces.repositories

import com.arnas.klatrebackend.dataclasses.AddGroupRequest
import com.arnas.klatrebackend.dataclasses.Group
import com.arnas.klatrebackend.dataclasses.GroupUser

interface GroupRepositoryInterface {

    fun getGroups(userID: Long): List<Group>
    fun addGroup(group: AddGroupRequest): Long
    fun deleteGroup(groupId: Long): Int
    fun getGroupUsers(groupId: Long): List<GroupUser>
    fun addUserToGroup(userId: Long, groupId: Long, role: Int): Int
    fun getUserGroupRole(userId: Long, groupId: Long): Int?
    fun updateUserGroupRole(userId: Long, groupId: Long, newRoleId: Int): Int
    fun deleteUserFromGroup(userId: Long, groupId: Long): Int
    fun getGroupById(id: Long): Group?
    fun getGroupByUuid(groupUuid: String): Group?
}