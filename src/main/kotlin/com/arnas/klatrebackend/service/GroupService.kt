package com.arnas.klatrebackend.service

import com.arnas.klatrebackend.dataclass.AddGroupRequest
import com.arnas.klatrebackend.dataclass.GradingSystem
import com.arnas.klatrebackend.dataclass.GroupUser
import com.arnas.klatrebackend.dataclass.GroupWithPlaces
import com.arnas.klatrebackend.dataclass.PlaceRequest
import com.arnas.klatrebackend.dataclass.Role
import com.arnas.klatrebackend.dataclass.ServiceResult
import com.arnas.klatrebackend.repository.GradingSystemRepository
import com.arnas.klatrebackend.repository.GroupRepository
import com.arnas.klatrebackend.repository.PlaceRepository
import com.arnas.klatrebackend.repository.UserRepository
import org.springframework.stereotype.Service

@Service
open class GroupService(
    private val groupRepository: GroupRepository,
    private val userRepository: UserRepository,
    private val placeRepository: PlaceRepository,
    private val gradingSystemRepository: GradingSystemRepository,
) {

    open fun getGroups(userID: Long): ServiceResult<List<GroupWithPlaces>> {
        val groups = groupRepository.getGroups(userID)
        val groupWithPlaces = groups.map { GroupWithPlaces(it, placeRepository.getPlacesByGroupId(it.id))}

        return ServiceResult(data = groupWithPlaces, message = "Groups retrieved successfully", success = true)
    }

    open fun addGroup(userID: Long, request: AddGroupRequest): ServiceResult<Long> {
        val name = request.name
        val personal: Boolean = (request.personal ?: ServiceResult(data = null, message = "Personal status is required", success = false)) as Boolean
        val group = AddGroupRequest(owner = userID, name = name, personal = personal)
        request.name.let { group.name = it }
        request.description?.let { group.description = it }
        val groupID = groupRepository.addGroup(group)
        val userIDs = request.invites?.mapNotNull { email ->
            userRepository.getUserByEmail(email)?.id
        } ?: emptyList()

        groupRepository.addUserToGroup(userID, groupID, 0)
        userIDs.forEach {
            groupRepository.addUserToGroup(it, groupID, 2)
        }
        return ServiceResult(data = groupID, message = "Group added successfully", success = true)
    }


    open fun addPlaceToGroup(groupID: Long, placeRequest: PlaceRequest) {
        groupRepository.addPlaceToGroup(groupID, placeRequest)
    }

    open fun deleteGroup(userID: Long, groupID: Long): ServiceResult<Unit> {
        val userRole = groupRepository.getUserGroupRole(userID, groupID)
            ?: return ServiceResult(success = false, message = "User is not a member of group")

        if (userRole != Role.OWNER.id) {
            return ServiceResult(success = false, message = "Only group owners can delete groups")
        }
        val rowsAffected = groupRepository.deleteGroup(groupID)
        return if (rowsAffected > 0) {
            ServiceResult(success = true, message = "Group deleted successfully")
        } else {
            ServiceResult(success = false, message = "Something went wrong when deleting group", errorCode = "401")
        }
    }

    open fun getGroupUserRole(userId: Long, groupId: Long): ServiceResult<Int?> {
        try {
            val role = groupRepository.getUserGroupRole(userId, groupId)
            if(role == null) {
                return ServiceResult(success = false, message = "User is not a member of group")
            }
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

    open fun changeGroupUserRole(userId: Long, newRoleId: Int, groupId: Long): ServiceResult<Unit> {
        try {
            require(groupRepository.updateUserGroupRole(userId, groupId, newRoleId) > 0) { "User is not a member of group" }
        } catch (e: Exception) {
            e.printStackTrace()
            return ServiceResult(success = false, message = "Error changing user role", data = null)
        }
        return ServiceResult(success = true, message = "User role changed successfully")
    }

    open fun removeUserFromGroup(userId: Long, groupId: Long): ServiceResult<Unit> {
        try {
            groupRepository.deleteUserFromGroup(userId, groupId)
            return ServiceResult(success = true, message = "User removed successfully")
        } catch (e: Exception) {
            return ServiceResult(success = false, message = "Error removing user from group", data = null)
        }
    }



    open fun getGradingSystemsInGroup(groupID: Long): List<GradingSystem> {
        return gradingSystemRepository.getGradingSystemsInGroup(groupID)
    }

    open fun getUsersInGroup(groupID: Long): ServiceResult<List<GroupUser>> {
        try {
            val result = groupRepository.getGroupUsers(groupID)
            return ServiceResult(success = true, data = result, message = "Users retrieved successfully")
        } catch (e: Exception) {
            e.printStackTrace()
            return ServiceResult(success = false, message = "Error getting users in group", data = null)
        }
    }

}