package com.arnas.klatrebackend.services

import com.arnas.klatrebackend.dataclasses.AddGroupRequest
import com.arnas.klatrebackend.dataclasses.GradingSystem
import com.arnas.klatrebackend.dataclasses.Group
import com.arnas.klatrebackend.dataclasses.GroupUser
import com.arnas.klatrebackend.dataclasses.GroupWithPlaces
import com.arnas.klatrebackend.dataclasses.PlaceRequest
import com.arnas.klatrebackend.dataclasses.Role
import com.arnas.klatrebackend.dataclasses.ServiceResult
import com.arnas.klatrebackend.interfaces.services.GroupServiceInterface
import com.arnas.klatrebackend.repositories.GradingSystemRepository
import com.arnas.klatrebackend.repositories.GroupRepository
import com.arnas.klatrebackend.repositories.PlaceRepository
import com.arnas.klatrebackend.repositories.UserRepository
import org.springframework.stereotype.Service

@Service
open class GroupService(
    private val groupRepository: GroupRepository,
    private val userRepository: UserRepository,
    private val placeRepository: PlaceRepository,
    private val gradingSystemRepository: GradingSystemRepository,
): GroupServiceInterface {

    override fun getGroups(userId: Long): ServiceResult<List<GroupWithPlaces>> {
        val groups = groupRepository.getGroups(userId)
        val groupWithPlaces = groups.map { GroupWithPlaces(it, placeRepository.getPlacesByGroupId(it.id))}

        return ServiceResult(data = groupWithPlaces, message = "Groups retrieved successfully", success = true)
    }

    override fun addGroup(userId: Long, request: AddGroupRequest): ServiceResult<Long> {
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
        return ServiceResult(data = groupID, message = "Group added successfully", success = true)
    }


    override fun addPlaceToGroup(groupId: Long, placeRequest: PlaceRequest): ServiceResult<Long> {
        val id = placeRepository.addPlaceToGroup(groupId, placeRequest.name, placeRequest.description)
        return ServiceResult(data = id, message = "Place added successfully", success = true)
    }

    override fun deleteGroup(userId: Long, groupId: Long): ServiceResult<Unit> {
        val userRole = groupRepository.getUserGroupRole(userId, groupId)
            ?: return ServiceResult(success = false, message = "User is not a member of group")

        if (userRole != Role.OWNER.id) {
            return ServiceResult(success = false, message = "Only group owners can delete groups")
        }
        val rowsAffected = groupRepository.deleteGroup(groupId)
        return if (rowsAffected > 0) {
            ServiceResult(success = true, message = "Group deleted successfully")
        } else {
            ServiceResult(success = false, message = "Something went wrong when deleting group", errorCode = "401")
        }
    }

    override fun getGroupUserRole(userId: Long, groupId: Long): ServiceResult<Int?> {
        try {
            val role = groupRepository.getUserGroupRole(userId, groupId) ?: return ServiceResult(
                success = false,
                message = "User is not a member of group"
            )
            return ServiceResult(
                success = true,
                message = "User role retrieved successfully",
                data = role
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return ServiceResult(success = false, message = "Error getting user role", data = null)
        }
    }

    override fun changeGroupUserRole(userId: Long, newRoleId: Int, groupId: Long): ServiceResult<Unit> {
        try {
            require(groupRepository.updateUserGroupRole(userId, groupId, newRoleId) > 0) { "User is not a member of group" }
        } catch (e: Exception) {
            e.printStackTrace()
            return ServiceResult(success = false, message = "Error changing user role", data = null)
        }
        return ServiceResult(success = true, message = "User role changed successfully")
    }

    override fun removeUserFromGroup(userId: Long, groupId: Long): ServiceResult<Unit> {
        try {
            groupRepository.deleteUserFromGroup(userId, groupId)
            return ServiceResult(success = true, message = "User removed successfully")
        } catch (e: Exception) {
            return ServiceResult(success = false, message = "Error removing user from group", data = null)
        }
    }

    override fun getGradingSystemsInGroup(groupId: Long): List<GradingSystem> {
        return gradingSystemRepository.getGradingSystemsInGroup(groupId)
    }

    override fun getUsersInGroup(groupId: Long): ServiceResult<List<GroupUser>> {
        try {
            val result = groupRepository.getGroupUsers(groupId)
            return ServiceResult(success = true, data = result, message = "Users retrieved successfully")
        } catch (e: Exception) {
            e.printStackTrace()
            return ServiceResult(success = false, message = "Error getting users in group", data = null)
        }
    }


    override fun getGroupByUuid(groupUuid: String): ServiceResult<Group> {
        try {
            val group = groupRepository.getGroupByUuid(groupUuid) ?: return ServiceResult(success = false, message = "Group not found", data = null)
            return ServiceResult(success = true, data = group, message = "Group retrieved successfully")
        } catch(exception: Exception) {
            exception.printStackTrace()
            return ServiceResult(success = false, message = "Error getting group by id", data = null)
        }

    }


}