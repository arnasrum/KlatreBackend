package com.arnas.klatrebackend.interfaces.services

import com.arnas.klatrebackend.dataclass.AddGroupRequest
import com.arnas.klatrebackend.dataclass.GradingSystem
import com.arnas.klatrebackend.dataclass.Group
import com.arnas.klatrebackend.dataclass.GroupUser
import com.arnas.klatrebackend.dataclass.GroupWithPlaces
import com.arnas.klatrebackend.dataclass.PlaceRequest
import com.arnas.klatrebackend.dataclass.ServiceResult

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