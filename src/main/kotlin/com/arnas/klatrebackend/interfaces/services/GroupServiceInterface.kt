package com.arnas.klatrebackend.interfaces.services

import com.arnas.klatrebackend.dataclasses.AddGroupRequest
import com.arnas.klatrebackend.dataclasses.GradingSystem
import com.arnas.klatrebackend.dataclasses.GroupUser
import com.arnas.klatrebackend.dataclasses.GroupWithPlaces
import com.arnas.klatrebackend.dataclasses.PlaceRequest
import com.arnas.klatrebackend.dataclasses.ServiceResult

interface GroupServiceInterface {
    fun getGroups(userId: Long): ServiceResult<List<GroupWithPlaces>>
    fun addGroup(userId: Long, request: AddGroupRequest): ServiceResult<Long>
    fun addPlaceToGroup(groupId: Long, placeRequest: PlaceRequest): ServiceResult<Long>
    fun deleteGroup(userId: Long, groupId: Long): ServiceResult<Unit>
    fun getGroupUserRole(userId: Long, groupId: Long): ServiceResult<Int?>
    fun changeGroupUserRole(userId: Long, newRoleId: Int, groupId: Long): ServiceResult<Unit>
    fun removeUserFromGroup(userId: Long, groupId: Long): ServiceResult<Unit>
    fun getGradingSystemsInGroup(groupId: Long): List<GradingSystem>
    fun getUsersInGroup(groupId: Long): ServiceResult<List<GroupUser>>
}