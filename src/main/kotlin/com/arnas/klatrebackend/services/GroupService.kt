package com.arnas.klatrebackend.services

import com.arnas.klatrebackend.annotation.RequireGroupAccess
import com.arnas.klatrebackend.dataclasses.AddGroupRequest
import com.arnas.klatrebackend.dataclasses.GradingSystem
import com.arnas.klatrebackend.dataclasses.Group
import com.arnas.klatrebackend.dataclasses.GroupUser
import com.arnas.klatrebackend.dataclasses.GroupWithPlaces
import com.arnas.klatrebackend.dataclasses.PlaceRequest
import com.arnas.klatrebackend.dataclasses.Role
import com.arnas.klatrebackend.dataclasses.ServiceResult
import com.arnas.klatrebackend.dataclasses.UnauthorizedException
import com.arnas.klatrebackend.interfaces.repositories.GradingSystemRepositoryInterface
import com.arnas.klatrebackend.interfaces.repositories.GroupRepositoryInterface
import com.arnas.klatrebackend.interfaces.repositories.PlaceRepositoryInterface
import com.arnas.klatrebackend.interfaces.repositories.UserRepositoryInterface
import com.arnas.klatrebackend.interfaces.services.GroupServiceInterface
import org.springframework.stereotype.Service

@Service
open class GroupService(
    private val groupRepository: GroupRepositoryInterface,
    private val userRepository: UserRepositoryInterface,
    private val placeRepository: PlaceRepositoryInterface,
    private val gradingSystemRepository: GradingSystemRepositoryInterface,
): GroupServiceInterface {

    override fun getGroups(userId: Long): List<GroupWithPlaces> {
        val groups = groupRepository.getGroups(userId)
        val groupWithPlaces = groups.map { GroupWithPlaces(it, placeRepository.getPlacesByGroupId(it.id))}
        return groupWithPlaces
    }

    override fun addGroup(userId: Long, request: AddGroupRequest): Long {
        val name = request.name
        val personal: Boolean = (request.personal ?: ServiceResult(data = null, message = "Personal status is required", success = false)) as Boolean
        val group = AddGroupRequest(owner = userId, name = name, personal = personal)
        request.name.let { group.name = it }
        request.description?.let { group.description = it }
        val groupID = groupRepository.addGroup(group)
        val userIDs = request.invites?.mapNotNull { email ->
            userRepository.getUserByEmail(email)?.id
        } ?: emptyList()

        groupRepository.addUserToGroup(userId, groupID, 0)
        userIDs.forEach {
            groupRepository.addUserToGroup(it, groupID, 2)
        }
        return groupID
    }


    @RequireGroupAccess(minRole = Role.ADMIN)
    override fun addPlaceToGroup(userId: Long, groupId: Long, placeRequest: PlaceRequest): Long {
        val id = placeRepository.addPlaceToGroup(groupId, placeRequest.name, placeRequest.description)
        return id
    }

    @RequireGroupAccess(minRole = Role.ADMIN)
    override fun deleteGroup(userId: Long, groupId: Long) {
        val rowsAffected = groupRepository.deleteGroup(groupId)
        return if (rowsAffected > 0) {
        } else {
            throw Exception("Something went wrong when deleting group")
        }
    }

    override fun getGroupUserRole(userId: Long, groupId: Long): Int? {
        return groupRepository.getUserGroupRole(userId, groupId)
    }

    @RequireGroupAccess(minRole = Role.ADMIN)
    override fun changeGroupUserRole(userId: Long, targetUserId: Long, newRoleId: Int, groupId: Long) {
        val actingUserRole = groupRepository.getUserGroupRole(userId, groupId)
            ?: throw UnauthorizedException("User is not a member of group")
        val targetUserRole = groupRepository.getUserGroupRole(targetUserId, groupId)
            ?: throw RuntimeException("Target user is not a member of group")
        if(actingUserRole >= targetUserRole) {
            throw UnauthorizedException("User has insufficient permissions to change role")
        }
        if(targetUserRole == newRoleId)
            return
        // Check if newRoleId is valid
        require(groupRepository.updateUserGroupRole(targetUserId, groupId, newRoleId) > 0) {
            "Failed to update user role"
        }
    }

    @RequireGroupAccess(minRole = Role.ADMIN)
    @Throws(UnauthorizedException::class, RuntimeException::class)
    override fun kickUserFromGroup(userId: Long, targetUserId: Long, groupId: Long) {
        val actingUserRole = groupRepository.getUserGroupRole(userId, groupId)
            ?: throw UnauthorizedException("User is not a member of group")
        val targetUserRole = groupRepository.getUserGroupRole(targetUserId, groupId)
            ?: throw RuntimeException("Target user is not a member of group")
        if(actingUserRole >= targetUserRole) {
            throw UnauthorizedException("User has insufficient permissions to change role")
        }
        groupRepository.deleteUserFromGroup(targetUserId, groupId)
    }


    override fun getGradingSystemsInGroup(groupId: Long): List<GradingSystem> {
        return gradingSystemRepository.getGradingSystemsInGroup(groupId)
    }

    @RequireGroupAccess
    override fun getUsersInGroup(userId: Long, groupId: Long): List<GroupUser> {
        return groupRepository.getGroupUsers(groupId)
    }

    override fun getGroupByUuid(groupUuid: String): Group {
        val group = groupRepository.getGroupByUuid(groupUuid)
            ?: throw IllegalArgumentException("Group not found")
        return group
    }
}